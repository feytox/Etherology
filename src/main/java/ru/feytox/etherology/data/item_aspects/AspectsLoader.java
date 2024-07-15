package ru.feytox.etherology.data.item_aspects;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.AspectContainerId;
import ru.feytox.etherology.magic.aspects.AspectContainerType;
import ru.feytox.etherology.registry.misc.ResourceReloaders;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AspectsLoader implements IdentifiableResourceReloadListener {

    // TODO: 11.07.2024 consider using codecs... before it's too late :skull:

    private static ImmutableMap<AspectContainerId, AspectContainer> cache = ImmutableMap.of();
    private static boolean isInitialized = false;

    public static Optional<AspectContainer> getAspects(ItemStack stack, boolean multiplyCount) {
        if (!isInitialized) return Optional.empty();

        // TODO: 25.03.2024 replace with serializers
        if (stack.getItem() instanceof PotionItem) return getPotionAspects(stack);
        if (stack.getItem() instanceof TippedArrowItem) return getTippedAspects(stack);
        AspectContainer itemAspects = getAspects(stack).orElse(null);
        if (itemAspects == null) return Optional.empty();

        if (multiplyCount) itemAspects = itemAspects.map(value -> value * stack.getCount());
        return Optional.of(itemAspects);
    }

    private static Optional<AspectContainer> getAspects(ItemStack stack) {
        val itemId = AspectContainerId.of(Registries.ITEM.getId(stack.getItem()), AspectContainerType.ITEM);
        return Optional.ofNullable(cache.get(itemId));
    }

    public static Optional<AspectContainer> getPotionAspects(ItemStack potionStack) {
        AspectContainerType type = AspectContainerType.POTION;
        if (potionStack.getItem() instanceof SplashPotionItem) type = AspectContainerType.SPLASH_POTION;
        if (potionStack.getItem() instanceof LingeringPotionItem) type = AspectContainerType.LINGERING_POTION;

        val potion = potionStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion().orElse(null);
        if (potion == null) return Optional.empty();

        Identifier id = Registries.POTION.getId(potion.value());
        if (id == null) return Optional.empty();

        val potionId = AspectContainerId.of(id, type);
        return Optional.ofNullable(cache.get(potionId));
    }

    public static Optional<AspectContainer> getTippedAspects(ItemStack tippedStack) {
        val potion = tippedStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion().orElse(null);
        if (potion == null) return Optional.empty();

        Identifier id = Registries.POTION.getId(potion.value());
        if (id == null) return Optional.empty();

        val tippedId = AspectContainerId.of(id, AspectContainerType.TIPPED_ARROW);
        return Optional.ofNullable(cache.get(tippedId));
    }

    public static Optional<AspectContainer> getEntityAspects(Entity entity) {
        val entityId = AspectContainerId.of(Registries.ENTITY_TYPE.getId(entity.getType()), AspectContainerType.ENTITY);
        return Optional.ofNullable(cache.get(entityId));
    }

    @Override
    public Identifier getFabricId() {
        return EIdentifier.of("etherology_aspects");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor executor) {
        isInitialized = false;

        return CompletableFuture.supplyAsync(() -> manager.findResources("etherology_aspects", fileName -> fileName.toString().endsWith(".json")), executor)
                .thenApplyAsync(resources -> {
                    List<CompletableFuture<AspectsRegistry>> tasks = new ObjectArrayList<>();

                    resources.forEach((fileName, resource) -> tasks.add(CompletableFuture.supplyAsync(() -> loadAspectsFile(fileName, resource), executor)));

                    return tasks;
                }, executor)
                .thenApplyAsync(tasks -> tasks.stream().map(CompletableFuture::join), executor)
                .thenApplyAsync(stream -> stream.reduce(AspectsRegistry::combine), executor)
                .thenApplyAsync(optionalRegistry -> optionalRegistry.orElseGet(AspectsRegistry::getEmptyRegistry), executor)
                .thenApplyAsync(AspectsRegistry::applyParents, executor)
                .thenComposeAsync(synchronizer::whenPrepared, executor)
                .thenAcceptAsync(itemAspects -> {
                    cache = ImmutableMap.copyOf(itemAspects);
                    isInitialized = true;
                }, executor);
    }

    public AspectsRegistry loadAspectsFile(Identifier fileName, Resource resource) {
        return ResourceReloaders.EGSON.fromJson(ResourceReloaders.loadFile(fileName, resource), AspectsRegistry.class);
    }
}

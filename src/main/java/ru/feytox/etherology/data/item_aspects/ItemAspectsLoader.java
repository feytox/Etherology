package ru.feytox.etherology.data.item_aspects;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import ru.feytox.etherology.registry.util.ResourceReloaders;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ItemAspectsLoader implements IdentifiableResourceReloadListener {
    private static ImmutableMap<Identifier, ItemAspectsContainer> cache = ImmutableMap.of();
    private static boolean isInitialized = false;

    public static Optional<ItemAspectsContainer> getAspectsOf(Item item) {
        if (!isInitialized) return Optional.empty();

        Identifier itemId = Registries.ITEM.getId(item);
        if (!cache.containsKey(itemId)) return Optional.empty();

        return Optional.ofNullable(cache.get(itemId));
    }

    public static Optional<ItemAspectsContainer> getAspectsOf(ItemStack stack) {
        ItemAspectsContainer itemAspects = getAspectsOf(stack.getItem()).orElse(null);
        if (itemAspects == null) return Optional.empty();

        itemAspects.map(value -> value * stack.getCount());
        return Optional.of(itemAspects);
    }

    @Override
    public Identifier getFabricId() {
        return new EIdentifier("item_aspects");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor executor) {
        isInitialized = false;

        return CompletableFuture.supplyAsync(() -> manager.findResources("etherology_aspects", fileName -> fileName.toString().endsWith(".json")), executor)
                .thenApplyAsync(resources -> {
                    List<CompletableFuture<ItemAspectsRegistry>> tasks = new ObjectArrayList<>();

                    resources.forEach((fileName, resource) -> tasks.add(CompletableFuture.supplyAsync(() -> loadAspectsFile(fileName, resource), executor)));

                    return tasks;
                }, executor)
                .thenApplyAsync(tasks -> tasks.stream()
                        .map(CompletableFuture::join)
                        .reduce(ItemAspectsRegistry::combine)
                        .orElseGet(ItemAspectsRegistry.EMPTY_SUPPLIER)
                        .getRegistryMap(), executor)
                .thenComposeAsync(synchronizer::whenPrepared, executor).thenAcceptAsync(itemAspects -> {
                    cache = ImmutableMap.copyOf(itemAspects);
                    isInitialized = true;
                }, executor);
    }

    public ItemAspectsRegistry loadAspectsFile(Identifier fileName, Resource resource) {
        return ResourceReloaders.EGSON.fromJson(ResourceReloaders.loadFile(fileName, resource), ItemAspectsRegistry.class);
    }
}

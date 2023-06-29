package ru.feytox.etherology.data.item_aspects;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import ru.feytox.etherology.registry.util.ResourceReloaders;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ItemAspectsLoader implements IdentifiableResourceReloadListener {
    private static Map<Identifier, ItemAspectsContainer> cache = Collections.emptyMap();
    private static boolean isInitialized = false;

    public static Optional<ItemAspectsContainer> getAspectsOf(Item item) {
        if (!isInitialized) return Optional.empty();

        Identifier itemId = Registries.ITEM.getId(item);
        return Optional.ofNullable(cache.getOrDefault(itemId, null));
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
                .thenCompose(synchronizer::whenPrepared).thenAcceptAsync(itemAspects -> {
                    cache = itemAspects;
                    isInitialized = true;
                });
    }

    public ItemAspectsRegistry loadAspectsFile(Identifier fileName, Resource resource) {
        return ResourceReloaders.EGSON.fromJson(ResourceReloaders.loadFile(fileName, resource), ItemAspectsRegistry.class);
    }
}

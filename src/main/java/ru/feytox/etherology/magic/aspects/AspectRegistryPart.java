package ru.feytox.etherology.magic.aspects;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record AspectRegistryPart(@NotNull Map<AspectContainerId, AspectEntry> aspectEntries) {

    public static final Codec<AspectRegistryPart> CODEC = Codec.unboundedMap(AspectContainerId.CODEC, AspectEntry.CODEC).xmap(AspectRegistryPart::new, AspectRegistryPart::aspectEntries);

    public Map<AspectContainerId, AspectContainer> applyParents() {
        Lookup lookup = new Lookup(aspectEntries, new Object2ObjectOpenHashMap<>());
        aspectEntries.keySet().forEach(lookup::get);
        return lookup.results;
    }

    public static Map<AspectContainerId, AspectContainer> merge(Map<AspectContainerId, AspectContainer> entry1, Map<AspectContainerId, AspectContainer> entry2) {
        entry2.forEach((key, value) -> entry1.merge(key, value, AspectContainer::add));
        return entry1;
    }

    public record Lookup(Map<AspectContainerId, AspectEntry> aspectEntries, Map<AspectContainerId, AspectContainer> results) {

        public AspectContainer get(AspectContainerId id) {
            if (results.containsKey(id)) return results.get(id);

            AspectContainer container = aspectEntries.get(id).toContainer(this);
            results.put(id, container);
            return container;
        }
    }
}

package ru.feytox.etherology.magic.aspectContainer;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.Etherology;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record AspectContainerRegistryPart(@NotNull Map<AspectContainerId, AspectContainerEntry> aspectEntries) {

    public static final Codec<AspectContainerRegistryPart> CODEC = Codec.unboundedMap(AspectContainerId.CODEC, AspectContainerEntry.CODEC).xmap(AspectContainerRegistryPart::new, AspectContainerRegistryPart::aspectEntries);

    public Map<AspectContainerId, AspectContainer> applyParents() {
        Lookup lookup = new Lookup(aspectEntries, new Object2ObjectOpenHashMap<>());
        aspectEntries.keySet().forEach(lookup::get);
        return lookup.results;
    }

    public static AspectContainerRegistryPart merge(AspectContainerRegistryPart part1, AspectContainerRegistryPart part2) {
        val entries = Stream.concat(part1.aspectEntries.entrySet().stream(), part2.aspectEntries.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (entry1, entry2) -> {
                    if (entry1.priority() > entry2.priority()) return entry1;
                    if (entry1.priority() < entry2.priority()) return entry2;
                    Etherology.ELOGGER.error("Found 2 aspect entries with the same ID and priority. Choosing the first one (most likely a random choice).");
                    return entry1;
                }, Object2ObjectOpenHashMap::new));
        return new AspectContainerRegistryPart(entries);
    }

    public record Lookup(Map<AspectContainerId, AspectContainerEntry> aspectEntries, Map<AspectContainerId, AspectContainer> results) {

        public AspectContainer get(AspectContainerId id) {
            if (results.containsKey(id)) return results.get(id);

            AspectContainerEntry entry = aspectEntries.get(id);
            if (entry == null) throw new NoSuchElementException("Could not find entry %s.".formatted(id.toString()));

            AspectContainer container = entry.toContainer(this);
            results.put(id, container);
            return container;
        }
    }
}

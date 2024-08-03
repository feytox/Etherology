package ru.feytox.etherology.magic.aspects;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.Etherology;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record AspectRegistryPart(@NotNull Map<AspectContainerId, AspectEntry> aspectEntries) {

    public static final Codec<AspectRegistryPart> CODEC = Codec.unboundedMap(AspectContainerId.CODEC, AspectEntry.CODEC).xmap(AspectRegistryPart::new, AspectRegistryPart::aspectEntries);

    public Map<AspectContainerId, AspectContainer> applyParents() {
        Lookup lookup = new Lookup(aspectEntries, new Object2ObjectOpenHashMap<>());
        aspectEntries.keySet().forEach(lookup::get);
        return lookup.results;
    }

    public static AspectRegistryPart merge(AspectRegistryPart part1, AspectRegistryPart part2) {
        val entries = Stream.concat(part1.aspectEntries.entrySet().stream(), part2.aspectEntries.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (entry1, entry2) -> {
                    if (entry1.priority() > entry2.priority()) return entry1;
                    if (entry1.priority() < entry2.priority()) return entry2;
                    Etherology.ELOGGER.error("Found 2 aspect entries with the same ID and priority. Choosing the first one (most likely a random choice).");
                    return entry1;
                }, Object2ObjectOpenHashMap::new));
        return new AspectRegistryPart(entries);
    }

    public record Lookup(Map<AspectContainerId, AspectEntry> aspectEntries, Map<AspectContainerId, AspectContainer> results) {

        public AspectContainer get(AspectContainerId id) {
            if (results.containsKey(id)) return results.get(id);

            AspectEntry entry = aspectEntries.get(id);
            if (entry == null) throw new NoSuchElementException("Could not find entry %s.".formatted(id.toString()));

            AspectContainer container = entry.toContainer(this);
            results.put(id, container);
            return container;
        }
    }
}

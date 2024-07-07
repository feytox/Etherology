package ru.feytox.etherology.util.misc;

import io.wispforest.owo.util.TagInjector;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.*;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.mixin.TagEntryAccessor;

import java.util.*;

/**
 * @see TagInjector
 */
@UtilityClass
public class TagExcludeUtil {

    private static final Map<TagInjector.TagLocation, Set<Identifier>> EXCLUDING = new Object2ObjectOpenHashMap<>();

    public static void exclude(TagKey<Item> itemTag, Item... items) {
        exclude(Registries.ITEM, itemTag, items);
    }

    @SafeVarargs
    public static <T> void exclude(Registry<T> registry, TagKey<T> tag, T... values) {
        EXCLUDING.computeIfAbsent(new TagInjector.TagLocation(TagManagerLoader.getPath(registry.getKey()), tag.id()), location -> new ObjectOpenHashSet<>())
                .addAll(Arrays.stream(values).map(registry::getId).toList());
    }

    public static Map<Identifier, List<TagGroupLoader.TrackedEntry>> applyExcluding(Map<Identifier, List<TagGroupLoader.TrackedEntry>> original, String dataType) {
        EXCLUDING.forEach((location, entries) -> {
            if (!dataType.equals(location.type())) return;
            List<TagGroupLoader.TrackedEntry> currentEntries = original.get(location.tagId());
            if (currentEntries == null) return;

            currentEntries.removeIf(entry -> entries.contains(((TagEntryAccessor) entry.entry()).getId()));
        });
        return original;
    }
}

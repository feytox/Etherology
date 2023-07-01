package ru.feytox.etherology.data.item_aspects;

import com.google.common.base.Suppliers;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.registry.util.ResourceReloaders;

import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ItemAspectsRegistry {

    public static final Supplier<ItemAspectsRegistry> EMPTY_SUPPLIER = Suppliers.memoize(() -> new ItemAspectsRegistry(new Object2ObjectOpenHashMap<>()));

    @Getter
    private final Map<Identifier, EtherAspectsContainer> registryMap;

    public ItemAspectsRegistry combine(ItemAspectsRegistry otherRegistry) {
        otherRegistry.registryMap.forEach((itemId, container) -> this.registryMap.merge(itemId, container, EtherAspectsContainer::combine));
        return this;
    }

    public static JsonDeserializer<ItemAspectsRegistry> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject root = json.getAsJsonObject();

            Map<Identifier, EtherAspectsContainer> registryMap = new Object2ObjectOpenHashMap<>();
            root.asMap().forEach((s, jsonElement) -> {
                Identifier itemId = new Identifier(s);
                EtherAspectsContainer container = ResourceReloaders.EGSON.fromJson(jsonElement.getAsJsonObject(), EtherAspectsContainer.class);
                if (!container.isEmpty()) registryMap.put(itemId, container);
            });

            return new ItemAspectsRegistry(registryMap);
        };
    }
}

package ru.feytox.etherology.data.item_aspects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.AspectContainerId;
import ru.feytox.etherology.registry.util.ResourceReloaders;

import java.util.Map;

@RequiredArgsConstructor
public class AspectsRegistry {

    @Getter(lazy = true)
    private static final AspectsRegistry emptyRegistry = new AspectsRegistry(new Object2ObjectOpenHashMap<>());

    private final Map<AspectContainerId, AspectContainer> registryMap;

    public AspectsRegistry combine(AspectsRegistry otherRegistry) {
        otherRegistry.registryMap.forEach((itemId, container) -> this.registryMap.merge(itemId, container, AspectContainer::combineOnReload));
        return this;
    }

    public Map<AspectContainerId, AspectContainer> applyParents() {
        Map<AspectContainerId, AspectContainer> result = new Object2ObjectOpenHashMap<>();
        registryMap.forEach((key, value) -> result.put(key, value.applyParents(registryMap)));
        return result;
    }

    public static JsonDeserializer<AspectsRegistry> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject root = json.getAsJsonObject();

            Map<AspectContainerId, AspectContainer> registryMap = new Object2ObjectOpenHashMap<>();
            root.asMap().forEach((s, jsonElement) -> {
                val containerId = AspectContainerId.of(s);
                AspectContainer container = ResourceReloaders.EGSON.fromJson(jsonElement.getAsJsonObject(), AspectContainer.class);
                registryMap.put(containerId, container);
            });

            return new AspectsRegistry(registryMap);
        };
    }
}

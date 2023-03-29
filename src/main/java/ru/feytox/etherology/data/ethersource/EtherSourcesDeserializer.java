package ru.feytox.etherology.data.ethersource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EtherSourcesDeserializer {
    public static final EtherSourcesDeserializer INSTANCE = new EtherSourcesDeserializer();

    private EtherSourcesDeserializer() {}

    public Map<Identifier, Float> deserialize(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, JsonElement> itemsJson = jsonObject.asMap();
        Map<Identifier, Float> result = new HashMap<>();

        itemsJson.forEach((itemId, jsonElement) -> {
            Identifier id = new Identifier(itemId);
            float value = jsonElement.getAsFloat();
            result.put(id, value);
        });

        return result;
    }
}

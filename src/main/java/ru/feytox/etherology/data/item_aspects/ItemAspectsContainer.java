package ru.feytox.etherology.data.item_aspects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.feytox.etherology.magic.aspects.EtherAspect;

import java.util.Map;

@RequiredArgsConstructor
public class ItemAspectsContainer {

    @Getter
    private final Map<EtherAspect, Integer> aspects;

    public ItemAspectsContainer combine(ItemAspectsContainer otherContainer) {
        otherContainer.aspects.forEach((otherAspect, otherValue) -> this.aspects.merge(otherAspect, otherValue, Math::min));
        return this;
    }

    public static JsonDeserializer<ItemAspectsContainer> deserializer() throws JsonParseException, IllegalArgumentException {
        return (json, type, context) -> {
            JsonObject root = json.getAsJsonObject();

            Map<EtherAspect, Integer> aspects = new Object2ObjectOpenHashMap<>();
            root.asMap().forEach((s, jsonElement) -> {
                EtherAspect aspect = EtherAspect.valueOf(s.toUpperCase());
                int value = jsonElement.getAsInt();
                if (value < 0) throw new IllegalArgumentException("The value of the aspect cannot be negative.");
                if (value == 0) aspects.put(aspect, value);
            });

            return new ItemAspectsContainer(aspects);
        };
    }
}

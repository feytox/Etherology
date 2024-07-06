package ru.feytox.etherology.datagen.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.mixin.ModelAccessor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

@UtilityClass
public class ModelUtil {

    public static void registerItemWithOverride(ItemModelGenerator generator, Model model, Identifier fileId, TextureMap textures, ModelOverride... overrides) {
        var modelCollector = generator.writer;
        Map<TextureKey, Identifier> textureMap = model.createTextureMap(textures);

        modelCollector.accept(fileId, () -> {
            JsonObject mainJson = new JsonObject();
            ((ModelAccessor) model).getParent().ifPresent((parentId) ->
                    mainJson.addProperty("parent", parentId.toString()));
            if (!textureMap.isEmpty()) {
                JsonObject jsonObject2 = new JsonObject();
                textureMap.forEach((textureKey, textureId) ->
                        jsonObject2.addProperty(textureKey.getName(), textureId.toString()));
                mainJson.add("textures", jsonObject2);
            }

            JsonArray overridesJson = new JsonArray();
            Arrays.stream(overrides).forEach(modelOverride -> {
                JsonObject jsonOverride = new JsonObject();
                JsonObject predicate = new JsonObject();
                predicate.addProperty(modelOverride.predicateName(), modelOverride.predicateValue());
                jsonOverride.add("predicate", predicate);
                jsonOverride.addProperty("model", modelOverride.replaceModelId().toString());
                overridesJson.add(jsonOverride);
            });
            mainJson.add("overrides", overridesJson);

            return mainJson;
        });
    }
}

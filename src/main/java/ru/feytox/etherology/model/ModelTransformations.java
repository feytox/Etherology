package ru.feytox.etherology.model;

import com.google.common.base.Charsets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * from <a href="https://github.com/TechReborn/TechReborn/blob/c31a40072503169acb4ad7b083c16a4ae183661b/src/client/java/techreborn/client/render/ModelHelper.java">source</a>
 */
public class ModelTransformations {
    public static final ModelTransformation DEFAULT_ITEM_TRANSFORMS = loadTransformFromJson(new Identifier("minecraft:models/item/generated"));
    public static final ModelTransformation HANDHELD_ITEM_TRANSFORMS = loadTransformFromJson(new Identifier("minecraft:models/item/handheld"));

    public static ModelTransformation loadTransformFromJson(Identifier location) {
        try {
            return JsonUnbakedModel.deserialize(getReaderForResource(location)).getTransformations();
        } catch (IOException exception) {
            Etherology.ELOGGER.warn("Can't load resource " + location);
            exception.printStackTrace();
            return null;
        }
    }

    private static Reader getReaderForResource(Identifier location) throws IOException {
        Identifier file = new Identifier(location.getNamespace(), location.getPath() + ".json");
        Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(file).orElseThrow();
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
    }
}

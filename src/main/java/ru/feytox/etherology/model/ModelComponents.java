package ru.feytox.etherology.model;

import com.google.common.base.Charsets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * from <a href="https://github.com/TechReborn/TechReborn/blob/c31a40072503169acb4ad7b083c16a4ae183661b/src/client/java/techreborn/client/render/ModelHelper.java">source</a>
 */
public class ModelComponents {
    // TODO: 02.01.2024 deprecate unused???
    public static final ModelTransformation DEFAULT_ITEM = loadTransformFromJson(new Identifier("minecraft:models/item/generated"));
    public static final ModelTransformation HANDHELD_ITEM = loadTransformFromJson(new Identifier("minecraft:models/item/handheld"));
    public static final ModelTransformation STAFF_ITEM = loadTransformFromJson(new EIdentifier("models/item/staff_core"));
    public static final ModelTransformation STAFF_ITEM_STREAM = loadTransformFromJson(new EIdentifier("models/item/staff_core_stream"));
    public static final ModelTransformation STAFF_ITEM_CHARGE = loadTransformFromJson(new EIdentifier("models/item/staff_core_charge"));

    @Nullable
    public static ModelTransformation loadTransformFromJson(Identifier location) {
        JsonUnbakedModel model = loadJsonModel(location);
        return model != null ? model.getTransformations() : null;
    }

    @Nullable
    public static JsonUnbakedModel loadJsonModel(Identifier location) {
        try {
            return JsonUnbakedModel.deserialize(getReaderForResource(location));
        } catch (IOException e) {
            Etherology.ELOGGER.warn("Can't load resource " + location);
            Etherology.ELOGGER.error(e.getMessage());
            return null;
        }
    }

    private static Reader getReaderForResource(Identifier location) throws IOException {
        Identifier file = new Identifier(location.getNamespace(), location.getPath() + ".json");
        Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(file).orElseThrow();
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
    }
}
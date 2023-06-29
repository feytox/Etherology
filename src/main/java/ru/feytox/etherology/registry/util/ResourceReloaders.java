package ru.feytox.etherology.registry.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.data.item_aspects.ItemAspectsContainer;
import ru.feytox.etherology.data.item_aspects.ItemAspectsLoader;
import ru.feytox.etherology.data.item_aspects.ItemAspectsRegistry;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

@UtilityClass
public class ResourceReloaders {
    public static final Gson EGSON;

    public static void registerClientResources() {
        // TODO: 28.06.2023 impl for smth for client. For example, aspect textures
    }

    public static void registerServerData() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ItemAspectsLoader());
    }

    public static JsonObject loadFile(Identifier location, Resource resource) {
        return JsonHelper.deserialize(EGSON, getFileContents(location, resource), JsonObject.class);
    }

    /**
     * @see software.bernie.geckolib.loading.FileLoader#getFileContents(Identifier, ResourceManager)
     */
    public static String getFileContents(Identifier fileName, Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        }
        catch (Exception e) {
            Etherology.ELOGGER.error("Couldn't load " + fileName, e);
            throw new RuntimeException(new FileNotFoundException(fileName.toString()));
        }
    }

    static {
        EGSON = new GsonBuilder().setLenient()
                .registerTypeAdapter(ItemAspectsRegistry.class, ItemAspectsRegistry.deserializer())
                .registerTypeAdapter(ItemAspectsContainer.class, ItemAspectsContainer.deserializer())
                .create();
    }
}

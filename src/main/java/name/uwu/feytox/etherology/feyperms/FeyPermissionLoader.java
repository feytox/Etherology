package name.uwu.feytox.etherology.feyperms;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FeyPermissionLoader extends JsonDataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static final FeyPermissionLoader INSTANCE = new FeyPermissionLoader();
    private Map<Identifier, Permission> permissions = ImmutableMap.of();
    // TODO: 25/02/2023 String -> Identifier
    private Map<String, PermissionDeserializer<?>> DESERIALIZERS = new HashMap<>();
    private boolean loaded = false;

    private FeyPermissionLoader() {
        super(GSON, "feypermissions");
    }

    public Map<Identifier, Permission> getPermissions() {
        if (!loaded) {
            LOGGER.error("FeyPermissions didn't reloaded!");
            return new HashMap<>();
        }
        return permissions;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        if (DESERIALIZERS.isEmpty()) PermissionManager.INSTANCE.registerDeserializers();

        ImmutableMap.Builder<Identifier, Permission> builder = ImmutableMap.builder();

        prepared.forEach((id, json) -> {
            try {
                String deserializerName = json.getAsJsonObject().get("type").getAsString();
                PermissionDeserializer<?> deserializer = getDeserializer(deserializerName);
                assert deserializer != null;
                Permission permission = deserializer.deserialize(id, json);
                LOGGER.info("{} has been loaded!", id);
                builder.put(id, permission);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse permission {}", id, e);
            }
        });

        permissions = builder.build();
        PermissionManager.INSTANCE.registerPermissions(permissions);
        loaded = true;
    }

    @Nullable
    public PermissionDeserializer<?> getDeserializer(String name) {
        return DESERIALIZERS.getOrDefault(name, null);
    }

    public void registerDeserializers(PermissionDeserializer<?>... deserializers) {
        Iterator<PermissionDeserializer<?>> deserializerIterator = Arrays.stream(deserializers).iterator();
        deserializerIterator.forEachRemaining(deserializer ->
                DESERIALIZERS.put(deserializer.getName(), deserializer));
    }

    public boolean isLoaded() {
        return loaded;
    }
}

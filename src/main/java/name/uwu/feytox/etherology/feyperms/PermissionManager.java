package name.uwu.feytox.etherology.feyperms;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import name.uwu.feytox.etherology.feyperms.permissions.EssenceVisualPermission;
import name.uwu.feytox.etherology.feyperms.permissions.XpPermissionDeserializer;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.*;

public class PermissionManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final PermissionManager INSTANCE = new PermissionManager();

    private Map<Identifier, Permission> registeredPermissions = new HashMap<>();

    private PermissionManager() {
    }

    public void registerPermissions(Map<Identifier, Permission> jsonPermissions) {
        register(new EssenceVisualPermission());
        registeredPermissions.putAll(jsonPermissions);
    }

    public void registerDeserializers() {
        FeyPermissionLoader.INSTANCE.registerDeserializers(
                new XpPermissionDeserializer()
        );
    }

    private void register(Permission permission) {
        registeredPermissions.put(permission.getId(), permission);
    }

    public List<Permission> get(String... names) {
        List<Identifier> ids = Lists.transform(Arrays.stream(names).toList(), EIdentifier::new);
        return get(ids);
    }

    public List<Permission> get(Identifier... ids) {
        return get(Arrays.stream(ids).toList());
    }

    private List<Permission> get(List<Identifier> ids) {
        List<Permission> result = new ArrayList<>();
        ids.forEach(id -> {
            if (registeredPermissions.containsKey(id)) {
                result.add(registeredPermissions.get(id));
            } else {
                LOGGER.error("Unexpected permission identifier {}", id);
            }
        });
        return result;
    }
}

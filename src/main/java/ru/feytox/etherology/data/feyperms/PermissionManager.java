package ru.feytox.etherology.data.feyperms;

import com.google.common.collect.Lists;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.data.feyperms.permissions.EssenceVisualPermission;
import ru.feytox.etherology.data.feyperms.permissions.XpPermissionDeserializer;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.*;

import static ru.feytox.etherology.Etherology.ELOGGER;

public class PermissionManager {
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
                ELOGGER.error("Unexpected permission identifier {}", id);
            }
        });
        return result;
    }
}

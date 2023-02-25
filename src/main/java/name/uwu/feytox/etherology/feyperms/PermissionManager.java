package name.uwu.feytox.etherology.feyperms;

import name.uwu.feytox.etherology.feyperms.permissions.EssenceVisualPermission;

import java.util.*;

public class PermissionManager {
    public static final PermissionManager INSTANCE = new PermissionManager();

    private Map<String, Permission> registeredPermissions = new HashMap<>();

    private PermissionManager() {
        registerPermissions();
    }

    private void registerPermissions() {
        register(new EssenceVisualPermission());
    }

    private void register(Permission permission) {
        registeredPermissions.put(permission.getName(), permission);
    }


    public List<Permission> get(String... names) {
        Iterator<String> namesIter = Arrays.stream(names).iterator();
        List<Permission> result = new ArrayList<>();
        namesIter.forEachRemaining(name -> {
            if (registeredPermissions.containsKey(name)) {
                result.add(registeredPermissions.get(name));
            }
        });
        return result;
    }
}

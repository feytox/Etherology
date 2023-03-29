package ru.feytox.etherology.data.feyperms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.List;

public interface Permissible {
    List<Permission> getPermissions();

    default boolean permTest(World world, PlayerEntity user) {
        List<Permission> usePermissions = getPermissions();
        for (Permission permission: usePermissions) {
            if (!permission.test(world, user) || world.isClient != permission.isClient()) return false;
        }
        return true;
    }
}

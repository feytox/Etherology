package ru.feytox.etherology.feyperms.permissions;

import ru.feytox.etherology.feyperms.Permission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class XpPermission extends Permission {
    private final int xp_level;

    public XpPermission(Identifier id, int xp_level) {
        super(id, true);
        this.xp_level = xp_level;
    }

    @Override
    public boolean test(World world, PlayerEntity player) {
        return player.experienceLevel >= xp_level;
    }

    @Override
    public boolean apply(World world, PlayerEntity player) {
        return false;
    }
}

package name.uwu.feytox.etherology.feyperms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class Permission {
    private String name;
    private boolean isClient;

    public Permission(String name, boolean isClient) {
        this.name = name;
        this.isClient = isClient;
    }

    public boolean isClient() {
        return isClient;
    }

    public String getName() {
        return name;
    }

    public abstract boolean test(World world, PlayerEntity player);
    public abstract boolean apply(World world, PlayerEntity player);
}

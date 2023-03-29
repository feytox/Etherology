package ru.feytox.etherology.data.feyperms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class Permission {
    private final Identifier id;
    private final boolean isClient;

    public Permission(Identifier id, boolean isClient) {
        this.id = id;
        this.isClient = isClient;
    }

    public boolean isClient() {
        return isClient;
    }

    public Identifier getId() {
        return id;
    }

    public abstract boolean test(World world, PlayerEntity player);
    public abstract boolean apply(World world, PlayerEntity player);
}

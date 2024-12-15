package ru.feytox.etherology.block.furniture;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

@Getter
public abstract class FurnitureData {

    protected final boolean isBottom;

    public FurnitureData(boolean isBottom) {
        this.isBottom = isBottom;
    }

    public static void updateData(ServerWorld world, BlockPos pos) {
        world.markDirty(pos);
        world.getChunkManager().markForUpdate(pos);
    }

    public void onUse(World world, BlockState state, BlockPos pos, PlayerEntity player, Vec2f hitPos, Hand hand) {}

    public abstract void readNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup registryLookup);

    public abstract void writeNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup registryLookup);
}

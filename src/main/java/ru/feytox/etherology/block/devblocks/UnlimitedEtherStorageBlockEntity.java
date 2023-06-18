package ru.feytox.etherology.block.devblocks;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import static ru.feytox.etherology.registry.block.DevBlocks.UNLIMITED_ETHER_STORAGE_BLOCK_ENTITY_TYPE;

public class UnlimitedEtherStorageBlockEntity extends TickableBlockEntity implements EtherStorage {
    public UnlimitedEtherStorageBlockEntity(BlockPos pos, BlockState state) {
        super(UNLIMITED_ETHER_STORAGE_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
    }

    @Override
    public float getMaxEther() {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getStoredEther() {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getTransferSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setStoredEther(float value) {}

    @Override
    public boolean isInputSide(Direction side) {
        return false;
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        return Direction.DOWN;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {
        if (world.getTime() % 5 == 0) transfer(world);
    }

    @Override
    public float decrement(float value) {
        return value;
    }

    @Override
    public boolean isActivated() {
        return false;
    }
}

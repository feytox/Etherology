package ru.feytox.etherology.block.etherealFork;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.ether.EtherFork;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;

import static ru.feytox.etherology.block.etherealChannel.EtherealChannelBlock.ACTIVATED;
import static ru.feytox.etherology.registry.block.BlocksRegistry.ETHEREAL_FORK_BLOCK_ENTITY;

public class EtherealForkBlockEntity extends TickableBlockEntity implements EtherFork {
    private float storedEther = 0;
    private List<Direction> cachedOutputSides = new ArrayList<>();

    public EtherealForkBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_FORK_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
    }

    @Override
    public List<Direction> getCachedOutputSides() {
        return cachedOutputSides;
    }

    @Override
    public void setCachedOutputSides(List<Direction> outputSides) {
        cachedOutputSides = outputSides;
    }

    @Override
    public float getMaxEther() {
        return 8;
    }

    @Override
    public float getStoredEther() {
        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public void setStoredEther(float value) {
        storedEther = value;
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        return null;
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
    public boolean isActivated() {
        return getCachedState().get(ACTIVATED);
    }
}

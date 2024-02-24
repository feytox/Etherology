package ru.feytox.etherology.magic.ether;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.block.etherealChannel.EtherealChannel;

import java.util.ArrayList;
import java.util.List;

public interface EtherFork extends EtherPipe {
    List<Direction> getCachedOutputSides();
    void setCachedOutputSides(List<Direction> outputSides);

    default List<Direction> getOutputSides(ServerWorld world) {
        BlockPos pos = getStoragePos();
        BlockState state = world.getBlockState(pos);
        return getOutputSides(state);
    }

    default List<Direction> getOutputSides(BlockState state) {
        List<Direction> outputSides = new ArrayList<>();

        if (state.get(EtherealChannel.NORTH).isOutput()) outputSides.add(Direction.NORTH);
        if (state.get(EtherealChannel.SOUTH).isOutput()) outputSides.add(Direction.SOUTH);
        if (state.get(EtherealChannel.WEST).isOutput()) outputSides.add(Direction.WEST);
        if (state.get(EtherealChannel.EAST).isOutput()) outputSides.add(Direction.EAST);
        if (state.get(EtherealChannel.DOWN).isOutput()) outputSides.add(Direction.DOWN);
        if (state.get(EtherealChannel.UP).isOutput()) outputSides.add(Direction.UP);

        return outputSides;
    }

    @Override
    default boolean canInputFrom(EtherStorage supplier) {
        return supplier instanceof EtherPipe;
    }

    @Override
    default boolean isInputSide(Direction side) {
        List<Direction> outputSides = getCachedOutputSides();
        return !outputSides.contains(side);
    }

    @Override
    default boolean isOutputSide(Direction direction) {
        return true;
    }

    @Override
    default void transfer(ServerWorld world) {
        float storedEther = getStoredEther();
        if (storedEther == 0 || isActivated()) return;

        List<Direction> outputSides = getOutputSides(world);
        setCachedOutputSides(outputSides);
        if (storedEther < outputSides.size()) return;

        outputSides.forEach(direction -> transferTo(world, direction));
    }
}

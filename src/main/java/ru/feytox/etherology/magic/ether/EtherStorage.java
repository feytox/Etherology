package ru.feytox.etherology.magic.ether;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import ru.feytox.etherology.blocks.etherealChannel.EtherealChannelBlockEntity;

import javax.annotation.Nullable;

public interface EtherStorage {
    float getMaxEther();
    float getStoredEther();
    float getTransferSize();

    /**
     * @deprecated используйте increment и decrement
     */
    void setStoredEther(float value);
    boolean isInputSide(Direction side);
    @Nullable
    Direction getOutputSide();
    BlockPos getStoragePos();
    void transferTick(ServerWorld world);

    default float getTransportableEther() {
        return getStoredEther();
    }

    default boolean isOutputSide(Direction direction) {
        Direction outputSide = getOutputSide();
        return outputSide != null && outputSide.equals(direction);
    }

    default boolean canInputFrom(EtherStorage supplier) {
        return true;
    }

    default boolean canOutputTo(EtherStorage consumer) {
        return consumer instanceof EtherPipe;
    }

    boolean isActivated();

    default void transfer(ServerWorld world) {
        float etherValue = getTransportableEther();
        if (etherValue == 0 || isActivated()) {
            if (this instanceof EtherealChannelBlockEntity channel) channel.setUseless(false);
            return;
        }

        Direction outputSide = getOutputSide();
        if (outputSide != null) transferTo(world, outputSide);
    }

    default void transferTo(ServerWorld world, Direction outputSide) {
        BlockPos pos = getStoragePos();
        Vec3i outputVec = outputSide.getVector();
        BlockPos nextPos = pos.add(outputVec);

        if (world.getBlockEntity(nextPos) instanceof EtherStorage consumer) {
            if (this instanceof EtherealChannelBlockEntity channel) channel.setUseless(false);

            if (!consumer.isInputSide(outputSide.getOpposite())) return;
            if (!consumer.canInputFrom(this)) return;
            if (!canOutputTo(consumer)) return;

            float transferSize = Math.min(getTransferSize(), consumer.getTransferSize());
            float points = decrement(transferSize);
            float excess = consumer.increment(points);
            increment(excess);
        } else if (this instanceof EtherealChannelBlockEntity channel) {
            channel.setUseless(getStoredEther() != 0);
            decrement(1);
        }
    }

    /**
     * @return излишек, который не поместился
     */
    default float increment(float value) {
        float storedEther = getStoredEther();
        float maxEther = getMaxEther();
        float newVal = Math.min(storedEther + value, maxEther);
        setStoredEther(newVal);
        return Math.max(0, storedEther + value - maxEther);
    }

    /**
     * @return количество эфира, который забрали
     */
    default float decrement(float value) {
        float storedEther = getStoredEther();
        float newVal = value <= storedEther ? storedEther - value : storedEther;
        setStoredEther(newVal);
        return storedEther - newVal;
    }
}

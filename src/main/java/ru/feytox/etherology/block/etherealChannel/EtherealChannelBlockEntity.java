package ru.feytox.etherology.block.etherealChannel;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.PipeSide;
import ru.feytox.etherology.magic.ether.EtherDisplay;
import ru.feytox.etherology.magic.ether.EtherPipe;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.List;

import static ru.feytox.etherology.block.etherealChannel.EtherealChannel.*;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL_BLOCK_ENTITY;

public class EtherealChannelBlockEntity extends TickableBlockEntity implements EtherPipe, EtherDisplay {

    private float storedEther = 0;
    @Getter @Setter
    private boolean isEvaporating = false;
    @Getter @Setter
    private boolean isCrossEvaporating = false;

    public EtherealChannelBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_CHANNEL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
    }

    @Override
    public boolean isActivated() {
        return getCachedState().get(ACTIVATED);
    }

    @Override
    public float getMaxEther() {
        return 1;
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

    @Override
    public boolean isInputSide(Direction side) {
        BlockState state = getCachedState();
        return !state.get(EtherealChannel.getAsOut(side)).isOutput();
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        List<EnumProperty<PipeSide>> properties = List.of(NORTH, SOUTH, EAST, WEST, UP, DOWN);
        BlockState state = getCachedState();
        for (EnumProperty<PipeSide> property : properties) {
            if (state.get(property).isOutput()) return Direction.byName(property.getName());
        }
        return null;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {
        if (world.getTime() % 5 != 0) return;
        transfer(world);
        syncData(world);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putFloat("stored_ether", storedEther);
        nbt.putBoolean("evaporating", isEvaporating);
        nbt.putBoolean("cross_evaporating", isCrossEvaporating);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        storedEther = nbt.getFloat("stored_ether");
        isEvaporating = nbt.getBoolean("evaporating");
        isCrossEvaporating = nbt.getBoolean("cross_evaporating");
    }

    @Override
    public float getDisplayEther() {
        return getStoredEther();
    }

    @Override
    public float getDisplayMaxEther() {
        return getMaxEther();
    }
}

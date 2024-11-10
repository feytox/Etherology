package ru.feytox.etherology.block.etherealChannel;

import lombok.Setter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.PipeSide;
import ru.feytox.etherology.magic.ether.EtherDisplay;
import ru.feytox.etherology.magic.ether.EtherPipe;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.List;

import static ru.feytox.etherology.block.etherealChannel.EtherealChannel.*;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL_BLOCK_ENTITY;

public class EtherealChannelBlockEntity extends TickableBlockEntity implements EtherPipe, EtherDisplay {
    private float storedEther = 0;
    @Setter
    private boolean isEvaporating = false;
    @Setter
    private boolean isCrossEvaporating = false;

    public EtherealChannelBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_CHANNEL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (state.get(ACTIVATED)) return;

        Direction outputDirection = getOutputSide();
        if (outputDirection == null) return;

        BlockPos outputPos = isCrossEvaporating ? pos.up() : pos;
        if (!isEvaporating) return;

        if (isCrossEvaporating && world.getBlockEntity(outputPos) instanceof EtherStorage consumer) {
            if (consumer.spawnCrossParticles(outputPos, world, outputDirection)) return;
        }

        spawnParticles(outputPos, world, outputDirection);
    }

    public static void spawnParticles(BlockPos pos, ClientWorld world, Direction direction) {
        if (world.getTime() % 4 != 0) return;
        Random random = world.getRandom();

        Vec3d channelVec = Vec3d.of(direction.getVector());
        Vec3d startPos = pos.toCenterPos().add(channelVec.multiply(0.5d));
        val particleType = random.nextFloat() < 0.25 ? EtherParticleTypes.ETHER_STAR : EtherParticleTypes.ETHER_DOT;
        val effect = new MovingParticleEffect(particleType, channelVec);
        effect.spawnParticles(world, random.nextBetween(1, 2), 0, startPos);
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

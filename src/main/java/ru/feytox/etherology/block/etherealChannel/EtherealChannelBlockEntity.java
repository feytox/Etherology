package ru.feytox.etherology.block.etherealChannel;

import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.feytox.etherology.enums.PipeSide;
import ru.feytox.etherology.magic.ether.EtherPipe;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.subtypes.LightSubtype;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.List;

import static ru.feytox.etherology.block.etherealChannel.EtherealChannel.*;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL_BLOCK_ENTITY;

public class EtherealChannelBlockEntity extends TickableBlockEntity implements EtherPipe {
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
        if (isEvaporating) {
            spawnParticles(outputPos, world, outputDirection);
        }
    }

    public static void spawnParticles(BlockPos pos, ClientWorld world, Direction direction) {
        if (world.getTime() % 2 != 0) return;
        Random random = world.getRandom();

        Vector3f vec = direction.getUnitVector();
        Vec3d startPos = new Vec3d(vec.mul(0.5f)).add(pos.toCenterPos());
        Vector3f endVec = vec.mul(2 + random.nextFloat() * 1.5f)
                .rotateY(random.nextFloat() - 0.5f)
                .add(0, random.nextFloat()*0.5f, 0);
        Vec3d endPos = new Vec3d(endVec).add(pos.toCenterPos());

        LightParticleEffect effect = new LightParticleEffect(ServerParticleTypes.LIGHT, LightSubtype.SIMPLE, endPos);
        effect.spawnParticles(world, 1, 0.07d, startPos);
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
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("stored_ether", storedEther);
        nbt.putBoolean("evaporating", isEvaporating);
        nbt.putBoolean("cross_evaporating", isCrossEvaporating);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        storedEther = nbt.getFloat("stored_ether");
        isEvaporating = nbt.getBoolean("evaporating");
        isCrossEvaporating = nbt.getBoolean("cross_evaporating");
    }
}

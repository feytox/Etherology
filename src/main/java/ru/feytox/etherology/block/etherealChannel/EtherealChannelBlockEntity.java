package ru.feytox.etherology.block.etherealChannel;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;
import ru.feytox.etherology.magic.ether.EtherPipe;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.particle.MovingParticle;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import javax.annotation.Nullable;

import static ru.feytox.etherology.Etherology.LIGHT;
import static ru.feytox.etherology.block.etherealChannel.EtherealChannelBlock.*;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL_BLOCK_ENTITY;

public class EtherealChannelBlockEntity extends TickableBlockEntity implements EtherPipe {
    private float storedEther = 0;
    private boolean isUseless = false;

    public EtherealChannelBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_CHANNEL_BLOCK_ENTITY, pos, state);
    }

    public void setUseless(boolean useless) {
        isUseless = useless;
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
        if (world.getBlockEntity(pos.add(outputDirection.getVector())) instanceof EtherStorage) return;

        if (isUseless) spawnParticles(world, outputDirection);
    }

    public void spawnParticles(ClientWorld world, Direction direction) {
        if (world.getTime() % 2 != 0) return;
        Random random = world.getRandom();

        Vector3f vec = direction.getUnitVector();
        Vec3d startPos = new Vec3d(vec.mul(0.5f)).add(pos.toCenterPos());
        Vector3f endVec = vec.mul(2 + random.nextFloat() * 1.5f)
                .rotateY(random.nextFloat() - 0.5f)
                .add(0, random.nextFloat()*0.5f, 0);
        Vec3d endPos = new Vec3d(endVec).add(pos.toCenterPos());

        MovingParticle.spawnParticles(world, LIGHT, 1, 0.07d,
                startPos.x, startPos.y, startPos.z, endPos.x, endPos.y, endPos.z, random);
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
        // TODO: 13/03/2023 по факту, можно возвращать всегда true, но ты подумай и проверь
        return true;
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        BlockState state = getCachedState();
        if (state.get(NORTH).isOutput()) return Direction.NORTH;
        if (state.get(SOUTH).isOutput()) return Direction.SOUTH;
        if (state.get(EAST).isOutput()) return Direction.EAST;
        if (state.get(WEST).isOutput()) return Direction.WEST;
        if (state.get(UP).isOutput()) return Direction.UP;
        if (state.get(DOWN).isOutput()) return Direction.DOWN;
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
        world.getChunkManager().markForUpdate(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("stored_ether", storedEther);
        nbt.putBoolean("is_useless", isUseless);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        storedEther = nbt.getFloat("stored_ether");
        isUseless = nbt.getBoolean("is_useless");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}

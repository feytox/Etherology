package ru.feytox.etherology.block.seal;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.seal.EssenceSupplier;
import ru.feytox.etherology.magic.seal.SealType;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

@Getter
public class SealBlockEntity extends TickableBlockEntity implements EssenceSupplier {

    private static final float MIN_POINTS = 64.0f;
    private static final float MAX_POINTS = 256.0f;
    private static final int MIN_RADIUS = 8;
    public static final int MAX_RADIUS = 24;

    private final SealType sealType;
    private int radius;
    private float points;
    private float maxPoints;
    private float pitch;
    private float yaw;

    public SealBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((SealBlock) state.getBlock()).getSealType());
    }

    public SealBlockEntity(BlockPos pos, BlockState state, SealType sealType) {
        super(EBlocks.SEAL_BLOCK_ENTITY, pos, state);
        this.sealType = sealType;

        float percent = Random.create().nextFloat();
        this.maxPoints = percent * (MAX_POINTS - MIN_POINTS) + MIN_POINTS;
        this.points = maxPoints;
        this.radius = (int) (percent * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS);
    }

    @Override
    public BlockPos getSupplierPos() {
        return getPos();
    }

    @Override
    public float getFillPercent() {
        return points / maxPoints;
    }

    public float getScale() {
        return (maxPoints - MIN_POINTS) / (MAX_POINTS - MIN_POINTS);
    }

    @Override
    public float decrement(ServerWorld world, float value) {
        float result = Math.min(points, value);
        points -= result;
        syncData(world);
        if (points <= 0) onEmptied(world);
        return result;
    }

    @Override
    public boolean isAlive() {
        return !isRemoved();
    }

    private void onEmptied(ServerWorld world) {
        world.breakBlock(pos, false);
    }

    public void setYaw(float yaw) {
        this.yaw = yaw % 360;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch % 360;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putFloat("max_points", maxPoints);
        nbt.putInt("radius", radius);
        nbt.putFloat("points", points);
        nbt.putFloat("pitch", pitch);
        nbt.putFloat("yaw", yaw);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        maxPoints = nbt.getFloat("max_points");
        radius = nbt.getInt("radius");
        points = nbt.getInt("points");
        pitch = nbt.getFloat("pitch");
        yaw = nbt.getFloat("yaw");
    }
}

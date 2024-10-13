package ru.feytox.etherology.block.zone;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.zones.EssenceSupplier;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

@Getter
public class ZoneCoreBlockEntity extends TickableBlockEntity implements EssenceSupplier {

    private static final float MIN_POINTS = 64.0f;
    private static final float MAX_POINTS = 256.0f;
    private static final int MIN_RADIUS = 8;
    public static final int MAX_RADIUS = 24;
    private static final int REFRESH_TIME = ZoneCoreRenderer.LIFETIME / 4;

    private final EssenceZoneType zoneType;
    private int radius;
    private float points;
    private float maxPoints;
    private float pitch;
    private float yaw;

    public ZoneCoreBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((ZoneCoreBlock) state.getBlock()).getZoneType());
    }

    public ZoneCoreBlockEntity(BlockPos pos, BlockState state, EssenceZoneType zoneType) {
        super(EBlocks.ZONE_CORE_BLOCK_ENTITY, pos, state);
        this.zoneType = zoneType;

        float percent = Random.create().nextFloat();
        this.maxPoints = percent * (MAX_POINTS - MIN_POINTS) + MIN_POINTS;
        this.points = maxPoints;
        this.radius = (int) (percent * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (world.getTime() % REFRESH_TIME == 0) ZoneCoreRenderer.refreshZone(this, pos, zoneType, world.getTime());
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

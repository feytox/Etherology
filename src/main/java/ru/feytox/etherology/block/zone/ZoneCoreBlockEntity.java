package ru.feytox.etherology.block.zone;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.zones.EssenceSupplier;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

public class ZoneCoreBlockEntity extends TickableBlockEntity implements EssenceSupplier {

    private static final float MIN_POINTS = 64.0f;
    private static final float MAX_POINTS = 256.0f;
    private static final int MIN_RADIUS = 8;
    public static final int MAX_RADIUS = 24;

    @Getter
    private final EssenceZoneType zoneType;
    @Getter
    private int radius;
    @Getter
    private float points;
    private float maxPoints;

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
    public float getFillPercent() {
        return points / maxPoints;
    }

    @Override
    public float decrement(ServerWorld world, float value) {
        float result = Math.min(points, value);
        points -= result;
        syncData(world);
        if (points <= 0) onEmptied(world);
        return result;
    }

    private void onEmptied(ServerWorld world) {
        world.breakBlock(pos, false);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putFloat("max_points", maxPoints);
        nbt.putInt("radius", radius);
        nbt.putFloat("points", points);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        maxPoints = nbt.getFloat("max_points");
        radius = nbt.getInt("radius");
        points = nbt.getInt("points");
    }
}

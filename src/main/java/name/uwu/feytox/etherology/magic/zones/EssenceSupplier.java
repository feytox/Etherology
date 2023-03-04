package name.uwu.feytox.etherology.magic.zones;

import name.uwu.feytox.etherology.util.feyapi.Deadable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface EssenceSupplier extends Deadable {
    float getPoints();
    int getCheckRadius();
    BlockPos getSupplierPos();
    List<EssenceDetectable> getCachedDetectable();
    void setCachedDetectable(List<EssenceDetectable> consumers);
    float decrement(float value);
    EssenceZones getZoneType();
    void tickRefresh(ServerWorld world);

    default void refreshDetectable(ServerWorld world) {
        if (getPoints() == 0) {
            clearConsumers();
            return;
        }

        int checkRadius = getCheckRadius();
        Vec3i radiusVec = new Vec3i(checkRadius, checkRadius, checkRadius);

        BlockPos centerPos = getSupplierPos();
        BlockPos minPos = centerPos.subtract(radiusVec);
        BlockPos maxPos = centerPos.add(radiusVec);
        Iterator<BlockPos> blockIter = BlockPos.iterate(minPos, maxPos).iterator();

        List<EssenceDetectable> result = new ArrayList<>();
        while (blockIter.hasNext()) {
            BlockPos blockPos = blockIter.next();
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof EssenceDetectable detectable) {
                if (blockPos.isWithinDistance(centerPos, detectable.getRadius())) {
                    detectable.sync(this);
                    result.add(detectable);
                }
            }
        }

        setCachedDetectable(result);
    }

    default void clearConsumers() {
        List<EssenceDetectable> cachedDetectable = getCachedDetectable();
        cachedDetectable.forEach(consumer -> {
            consumer.unsync(this);
        });
    }
}

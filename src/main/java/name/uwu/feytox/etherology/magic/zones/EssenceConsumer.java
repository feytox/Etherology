package name.uwu.feytox.etherology.magic.zones;

import name.uwu.feytox.etherology.util.feyapi.Deadable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface EssenceConsumer extends Deadable, EssenceDetectable {
    // TODO: 22/02/2023 Изменять по мере необходимости
    int MAX_RADIUS = 15;

    float getConsumingValue();
    float getRadius();
    BlockPos getDetectablePos();
    @Nullable
    EssenceSupplier getCachedSupplier();
    void setCachedSupplier(EssenceSupplier supplier);
    EssenceZones getZoneType();
    void setZoneType(EssenceZones zoneType);
    void increment(float value);

    default boolean tickConsume() {
        EssenceSupplier supplier = getCachedSupplier();
        if (supplier == null || supplier.isDead()) return false;

        float consumedPoints = supplier.decrement(getConsumingValue());
        increment(consumedPoints);

        return true;
    }

    default void setEmpty() {
        setZoneType(EssenceZones.NULL);
    }

    default boolean isEmpty() {
        return getZoneType().equals(EssenceZones.NULL);
    }

    default boolean checkZoneType(EssenceZones zoneType) {
        EssenceZones consumerZoneType = getZoneType();
        return consumerZoneType.equalsAny(EssenceZones.NULL, zoneType);
    }

    default boolean isTaken() {
        EssenceSupplier cachedSupplier = getCachedSupplier();
        return cachedSupplier != null && !cachedSupplier.isDead();
    }

    default boolean sync(EssenceSupplier supplier) {
        if (isTaken() || !checkZoneType(supplier.getZoneType())) return false;
        setCachedSupplier(supplier);
        setZoneType(supplier.getZoneType());
        return true;
    }

    default void unsync(EssenceSupplier supplier) {
        if (getCachedSupplier() == supplier) {
            setCachedSupplier(null);
        }
    }
}

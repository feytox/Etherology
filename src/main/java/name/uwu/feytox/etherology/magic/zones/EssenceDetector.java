package name.uwu.feytox.etherology.magic.zones;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;


public interface EssenceDetector extends EssenceDetectable {
    @Nullable
    EssenceSupplier getCachedSupplier();
    void setCachedSupplier(EssenceSupplier supplier);
    void tickSupplier(ServerWorld world, BlockPos blockPos, BlockState state);

    default boolean checkSupplier() {
        EssenceSupplier supplier = getCachedSupplier();
        return supplier != null && !supplier.isDead();
    }

    default float getSupplierPercent() {
        EssenceSupplier supplier = getCachedSupplier();
        if (!checkSupplier() || supplier == null) return 0;
        return MathHelper.clamp(supplier.getPoints() / 128, 0, 1);
    }

    @Override
    default boolean sync(EssenceSupplier supplier) {
        // TODO: 03/03/2023 Узнать, нужно ли проверять, насколько близко новый коннект
        if (checkSupplier()) return false;
        setCachedSupplier(supplier);
        return true;
    }

    default void unsync(EssenceSupplier supplier) {
        if (getCachedSupplier() == supplier) {
            setCachedSupplier(null);
        }
    }
}

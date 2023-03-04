package name.uwu.feytox.etherology.magic.zones;

import net.minecraft.util.math.BlockPos;

public interface EssenceDetectable {
    float getRadius();
    boolean sync(EssenceSupplier supplier);
    void unsync(EssenceSupplier supplier);
    BlockPos getDetectablePos();
}

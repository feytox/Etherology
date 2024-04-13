package ru.feytox.etherology.util.misc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public interface UniqueProvider {

    void setCachedUniqueOffset(Float value);
    @Nullable
    Float getCachedUniqueOffset();

    default float getUniqueOffset(BlockPos pos) {
        if (getCachedUniqueOffset() != null) return getCachedUniqueOffset();

        float sum = 0.0f;
        sum += pos.getX() % 32;
        sum += pos.getY() % 64;
        sum += pos.getZ() % 128;
        float unique = MathHelper.abs(sum) / 10.0f;
        float result = 2 * MathHelper.PI * unique;
        setCachedUniqueOffset(result);
        return result;
    }
}

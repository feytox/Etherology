package name.uwu.feytox.lotyh.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LVec3d {
    public static Vec3d of(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }
}

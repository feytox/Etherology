package name.uwu.feytox.etherology.util.feyapi;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EVec3d {
    public static Vec3d of(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }
}

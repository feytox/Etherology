package name.uwu.feytox.etherology.util.feyapi;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EVec3d {
    public static Vec3d of(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<Vec3d> listOf(Vec3d startPos, Vec3d endPos, double step) {
        List<Vec3d> result = new ArrayList<>();
        for (double x = Math.min(startPos.x, endPos.x); x <= Math.max(startPos.x, endPos.x); x += step) {
            for (double y = Math.min(startPos.y, endPos.y); y <= Math.max(startPos.y, endPos.y); y += step) {
                for (double z = Math.min(startPos.z, endPos.z); z <= Math.max(startPos.z, endPos.z); z += step) {
                    result.add(new Vec3d(x, y, z));
                }
            }
        }
        return result;
    }
}

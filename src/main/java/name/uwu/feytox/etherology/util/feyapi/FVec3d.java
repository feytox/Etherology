package name.uwu.feytox.etherology.util.feyapi;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class FVec3d extends Vec3d {
    public FVec3d(double x, double y, double z) {
        super(x, y, z);
    }

    public static FVec3d of(Vec3d vec3d) {
        return new FVec3d(vec3d.x, vec3d.y, vec3d.z);
    }

    public float getAngleX() {
        return getAngle(x, this.length());
    }

    public float getAngleY() {
        return getAngle(y, this.length());
    }

    public float getAngleZ() {
        return getAngle(z, this.length());
    }

    private static float getAngle(double c, double cProjection) {
        double cos = c / cProjection;
        return (float) Math.acos(cos);
    }

    public void write(PacketByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public static FVec3d read(PacketByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new FVec3d(x, y, z);
    }
}

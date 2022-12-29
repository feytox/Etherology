package name.uwu.feytox.etherology.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class NbtCoord {
    public String name;
    public double x;
    public double y;
    public double z;

    public NbtCoord(String name, double x, double y, double z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putDouble(name + "_x", x);
        nbt.putDouble(name + "_y", y);
        nbt.putDouble(name + "_z", z);
    }

    public static NbtCoord readNbt(String name, NbtCompound nbt) {
        double x = nbt.getDouble(name + "_x");
        double y = nbt.getDouble(name + "_y");
        double z = nbt.getDouble(name + "_z");
        return new NbtCoord(name, x, y, z);
    }

    public Vec3d asVector() {
        return new Vec3d(x, y, z);
    }
}

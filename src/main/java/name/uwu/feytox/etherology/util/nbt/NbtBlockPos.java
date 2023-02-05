package name.uwu.feytox.etherology.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class NbtBlockPos extends BlockPos implements Nbtable {
    private final String name;

    public NbtBlockPos(String name, BlockPos blockPos) {
        super(blockPos);
        this.name = name;
    }

    public NbtBlockPos(BlockPos blockPos) {
        this("block_pos", blockPos);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound subNbt = new NbtCompound();

        subNbt.putInt("x", getX());
        subNbt.putInt("y", getY());
        subNbt.putInt("z", getZ());

        nbt.put(name, subNbt);
    }

    public static NbtBlockPos readFromNbt(String name, NbtCompound nbt) {
        NbtCompound subNbt = nbt.getCompound(name);

        int x = subNbt.getInt("x");
        int y = subNbt.getInt("y");
        int z = subNbt.getInt("z");

        return new NbtBlockPos(new BlockPos(x, y, z));
    }

    public static NbtBlockPos readFromNbt(NbtCompound nbt) {
        return readFromNbt("block_pos", nbt);
    }

    @Override
    public Nbtable readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }
}

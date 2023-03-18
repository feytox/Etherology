package ru.feytox.etherology.util.nbt;

import net.minecraft.nbt.NbtCompound;

public interface Nbtable {
    void writeNbt(NbtCompound nbt);
    Nbtable readNbt(NbtCompound nbt);
}

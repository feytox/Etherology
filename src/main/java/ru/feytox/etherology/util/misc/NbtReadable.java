package ru.feytox.etherology.util.misc;

import net.minecraft.nbt.NbtCompound;

public interface NbtReadable<T> {

    void writeNbt(NbtCompound nbt);
    T readNbt(NbtCompound nbt);
}

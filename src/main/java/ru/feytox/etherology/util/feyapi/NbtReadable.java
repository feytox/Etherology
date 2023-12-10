package ru.feytox.etherology.util.feyapi;

import net.minecraft.nbt.NbtCompound;

@Deprecated
public interface NbtReadable<T> {

    void writeNbt(NbtCompound nbt);
    T readNbt(NbtCompound nbt);
}

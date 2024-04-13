package ru.feytox.etherology.util.misc;

import net.minecraft.nbt.NbtCompound;

// TODO: 29.08.2023 replace to NbtReadable
@Deprecated
public interface Nbtable {
    void writeNbt(NbtCompound nbt);
    Nbtable readNbt(NbtCompound nbt);
}

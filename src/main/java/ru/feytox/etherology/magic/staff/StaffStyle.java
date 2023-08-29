package ru.feytox.etherology.magic.staff;

import net.minecraft.nbt.NbtCompound;
import ru.feytox.etherology.util.feyapi.NbtReadable;

public enum StaffStyle implements NbtReadable<StaffStyle> {
    NULL,
    ARISTOCRAT,
    ASTRONOMY,
    HEAVENLY,
    OCULAR,
    RITUAL,
    ROYAL,
    TRADITIONAL;

    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("style", getName());
    }

    @Override
    public StaffStyle readNbt(NbtCompound nbt) {
        return StaffStyle.valueOf(nbt.getString("style").toUpperCase());
    }
}

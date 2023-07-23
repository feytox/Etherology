package ru.feytox.etherology.magic.zones;

import lombok.Getter;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.nbt.Nbtable;

public class EssenceZone implements Nbtable {

    @Getter
    private float value;

    public EssenceZone(float value) {
        this.value = value;
    }

    public void decrement(float value) {
        this.value -= value;
    }

    @Nullable
    public static EssenceZone readFromNbt(NbtCompound nbt) {
        if (nbt.isEmpty()) return null;
        return new EssenceZone(nbt.getFloat("essence_value"));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("essence_value", value);
    }

    @Override
    public Nbtable readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }
}

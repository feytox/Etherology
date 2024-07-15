package ru.feytox.etherology.magic.zones;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NbtCompound;

// TODO: 08.07.2024 consider replacing with just float storing
@Deprecated
public class EssenceZone {

    @Setter
    @Getter
    private float value;

    public EssenceZone(float value) {
        this.value = value;
    }

    public static EssenceZone readFromNbt(NbtCompound nbt) {
        if (nbt.isEmpty()) return null;
        return new EssenceZone(nbt.getFloat("essence_value"));
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("essence_value", value);
    }
}

package ru.feytox.etherology.magic.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.NbtCompound;
import ru.feytox.etherology.util.feyapi.NbtReadable;

@AllArgsConstructor
@NoArgsConstructor
public enum StaffPart implements NbtReadable<StaffPart> {
    NULL,
    CORE(true),
    DECOR,
    HEAD,
    LENSE(true),
    TIP;

    // TODO: 29.08.2023 deprecate
    @Getter
    private boolean hardCode = false;

    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("part", getName());
    }

    @Override
    public StaffPart readNbt(NbtCompound nbt) {
        return StaffPart.valueOf(nbt.getString("part").toUpperCase());
    }
}

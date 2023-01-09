package name.uwu.feytox.etherology.enums;

import name.uwu.feytox.etherology.util.EEquality;
import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;
import java.util.List;

public enum ArmillarStateType implements EEquality {
    OFF,
    RAISING,
    STORING,
    DAMAGING,
    CRAFTING,
    LOWERING;

    public static ArmillarStateType getByIndex(int index) {
        List<ArmillarStateType> values = Arrays.stream(ArmillarStateType.values()).toList();
        return index >= values.size() ? OFF : values.get(index);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("armillar_state_type", this.ordinal());
    }

    public static ArmillarStateType readNbt(NbtCompound nbt) {
        return getByIndex(nbt.getInt("armillar_state_type"));
    }
}

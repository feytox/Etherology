package name.uwu.feytox.etherology.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class FloatComponent implements IFloatComponent {
    protected float value;
    protected PlayerEntity player;

    public FloatComponent(PlayerEntity player, float def_value) {
        this.player = player;
        this.value = def_value;
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void increment(float value) {
        this.value += value;
    }

    @Override
    public void decrement(float value) {
        this.increment(-value);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getFloat("value");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("value", this.value);
    }
}

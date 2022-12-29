package name.uwu.feytox.etherology.components;

import net.minecraft.nbt.NbtCompound;

public class BooleanComponent implements IBooleanComponent {
    private boolean value;

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getBoolean("value");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("value", this.value);
    }
}

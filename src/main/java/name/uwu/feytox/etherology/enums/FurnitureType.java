package name.uwu.feytox.etherology.enums;

import name.uwu.feytox.etherology.blocks.closet.ClosetData;
import name.uwu.feytox.etherology.furniture.FurnitureData;
import name.uwu.feytox.etherology.util.nbt.Nbtable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.StringIdentifiable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public enum FurnitureType implements StringIdentifiable, Nbtable {
    EMPTY(null),
    CLOSET(ClosetData::new),
    FURNITURE(null);

    private final Factory<? extends FurnitureData> factory;

    FurnitureType(Factory<? extends FurnitureData> factory) {
        this.factory = factory;
    }

    @Nullable
    public FurnitureData createDataInstance(boolean isBottom) {
        return factory == null ? null : factory.create(isBottom);
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }

    public static FurnitureType getByIndex(int index) {
        List<FurnitureType> values = Arrays.stream(FurnitureType.values()).toList();
        return index >= values.size() ? EMPTY : values.get(index);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("furniture_type", this.ordinal());
    }

    public static FurnitureType readFromNbt(NbtCompound nbt) {
        return getByIndex(nbt.getInt("furniture_type"));
    }

    @Override
    public Nbtable readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }


    @FunctionalInterface
    public interface Factory<T extends FurnitureData> {
        T create(boolean isBottom);
    }
}

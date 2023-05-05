package ru.feytox.etherology.enums;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.block.closet.ClosetData;
import ru.feytox.etherology.block.shelf.ShelfData;
import ru.feytox.etherology.furniture.FurnitureData;
import ru.feytox.etherology.util.feyapi.EEquality;
import ru.feytox.etherology.util.nbt.Nbtable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public enum FurnitureType implements StringIdentifiable, Nbtable, EEquality {
    EMPTY(null),
    FURNITURE(null),
    CLOSET(ClosetData::new),
    SHELF(ShelfData::new);

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

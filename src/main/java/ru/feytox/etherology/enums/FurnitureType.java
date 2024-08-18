package ru.feytox.etherology.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.closet.ClosetData;
import ru.feytox.etherology.block.furniture.FurnitureData;
import ru.feytox.etherology.block.shelf.ShelfData;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum FurnitureType implements StringIdentifiable {
    EMPTY(null, true, null),
    FURNITURE(null, false, () -> EBlocks.FURNITURE_SLAB),
    CLOSET(ClosetData::new, false, () -> EBlocks.CLOSET_SLAB),
    SHELF(ShelfData::new, true, () -> EBlocks.SHELF_SLAB);

    private final Factory<? extends FurnitureData> factory;
    @Getter
    private final boolean sidedTransparent;
    @Nullable
    private final Supplier<Block> blockSupplier;

    @Nullable
    public FurnitureData createDataInstance(boolean isBottom) {
        return factory == null ? null : factory.create(isBottom);
    }

    @Nullable
    public Block getBlock() {
        return blockSupplier == null ? null : blockSupplier.get();
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

    public static Block[] getBlocks() {
        return Arrays.stream(values()).map(FurnitureType::getBlock).filter(Objects::nonNull).toArray(Block[]::new);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("furniture_type", this.ordinal());
    }

    public static FurnitureType readFromNbt(NbtCompound nbt) {
        return getByIndex(nbt.getInt("furniture_type"));
    }

    @FunctionalInterface
    public interface Factory<T extends FurnitureData> {
        T create(boolean isBottom);
    }
}

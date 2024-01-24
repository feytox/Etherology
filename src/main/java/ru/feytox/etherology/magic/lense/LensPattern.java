package ru.feytox.etherology.magic.lense;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LensPattern {

    // TODO: 24.01.2024 revise cell storage strategy from id-based to intpos-based.
    @NonNull
    private final Set<Integer> cracks;
    @NonNull
    private final Set<Integer> softCells;

    public static LensPattern empty() {
        return new LensPattern(new ObjectArraySet<>(), new ObjectArraySet<>());
    }

    public int getTextureOffset(int index) {
        if (isHard(index)) return 1;
        if (isSoft(index)) return 2;
        return 0;
    }

    public boolean isSoft(int index) {
        return softCells.contains(index);
    }

    public boolean isHard(int index) {
        return cracks.contains(index);
    }

    public boolean markSoft(int index) {
        return !isHard(index) && softCells.add(index);
    }

    public void unSoft(int index) {
        softCells.remove(index);
    }

    public boolean markHard(int index) {
        return !isSoft(index) && cracks.add(index);
    }

    public LensPattern copy() {
        return new LensPattern(new ObjectArraySet<>(cracks), new ObjectArraySet<>(softCells));
    }

    public NbtCompound writeNbt() {
        return writeNbt(new NbtCompound());
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        writeCells(nbt, "cracks", cracks);
        writeCells(nbt, "soft_cells", softCells);
        return nbt;
    }

    @Nullable
    public static LensPattern readNbt(NbtCompound nbt) {
        Set<Integer> cracks = readCells(nbt, "cracks");
        Set<Integer> softCells = readCells(nbt, "soft_cells");
        // TODO: 22.01.2024 maybe replace with try-catch or smth else
        if (cracks == null || softCells == null) return null;
        return new LensPattern(cracks, softCells);
    }

    private static void writeCells(NbtCompound nbt, String key, Set<Integer> intSet) {
        int[] arr = intSet.stream().mapToInt(Number::intValue).toArray();
        NbtIntArray cellsArr = new NbtIntArray(arr);
        nbt.put(key, cellsArr);
    }

    @Nullable
    private static Set<Integer> readCells(NbtCompound nbt, String key) {
        NbtElement element = nbt.get(key);
        if (!(element instanceof NbtIntArray nbtArr)) return null;

        return nbtArr.stream()
                .map(NbtInt::intValue)
                .collect(Collectors.toCollection(ObjectArraySet::new));
    }
}

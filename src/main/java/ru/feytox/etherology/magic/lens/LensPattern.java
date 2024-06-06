package ru.feytox.etherology.magic.lens;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

@EqualsAndHashCode
@RequiredArgsConstructor
public class LensPattern implements Cloneable {

    @NonNull
    private final IntArraySet cracks;
    @NonNull
    private final IntArraySet softCells;

    public static LensPattern empty() {
        return new LensPattern(new IntArraySet(), new IntArraySet());
    }

    public boolean isCracked() {
        return !cracks.isEmpty() || !softCells.isEmpty();
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

    public NbtCompound writeNbt() {
        val nbt = new NbtCompound();
        writeCells(nbt, "cracks", cracks);
        writeCells(nbt, "soft_cells", softCells);
        return nbt;
    }

    @Nullable
    public static LensPattern readNbt(NbtCompound nbt) {
        IntArraySet cracks = readCells(nbt, "cracks");
        IntArraySet softCells = readCells(nbt, "soft_cells");
        // TODO: 22.01.2024 maybe replace with try-catch or smth else
        if (cracks == null || softCells == null) return null;
        return new LensPattern(cracks, softCells);
    }

    private static void writeCells(NbtCompound nbt, String key, IntArraySet intSet) {
        int[] arr = intSet.toArray(new int[]{});
        NbtIntArray cellsArr = new NbtIntArray(arr);
        nbt.put(key, cellsArr);
    }

    @Nullable
    private static IntArraySet readCells(NbtCompound nbt, String key) {
        NbtElement element = nbt.get(key);
        if (!(element instanceof NbtIntArray nbtArr)) return null;

        return nbtArr.stream()
                .map(NbtInt::intValue)
                .collect(Collectors.toCollection(IntArraySet::new));
    }

    public void writeBuf(PacketByteBuf buf) {
        writeIntSet(buf, cracks);
        writeIntSet(buf, softCells);
    }

    public static LensPattern readBuf(PacketByteBuf buf) {
        return new LensPattern(readIntSet(buf), readIntSet(buf));
    }

    private void writeIntSet(PacketByteBuf buf, IntArraySet intSet) {
        buf.writeVarInt(intSet.size());
        intSet.forEach(buf::writeVarInt);
    }

    private static IntArraySet readIntSet(PacketByteBuf buf) {
        int size = buf.readVarInt();
        IntArraySet intSet = new IntArraySet();

        for (int i = 0; i < size; i++) {
            intSet.add(buf.readVarInt());
        }

        return intSet;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod") // TODO: 27.01.2024 huh?
    public LensPattern clone() {
        return new LensPattern(cracks.clone(), softCells.clone());
    }
}

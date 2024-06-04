package ru.feytox.etherology.magic.lens;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class LensModifiersData {

    @NonNull
    private final Map<Identifier, Integer> modifiers;

    public static LensModifiersData empty() {
        return new LensModifiersData(new Object2IntOpenHashMap<>());
    }

    public int getLevel(LensModifier modifier) {
        return modifiers.getOrDefault(modifier.modifierId(), 0);
    }

    public void setLevel(LensModifier modifier, int level) {
        if (level == 0) removeModifier(modifier);
        else modifiers.put(modifier.modifierId(), level);
    }

    public void removeModifier(LensModifier modifier) {
        if (!modifiers.containsKey(modifier.modifierId())) return;
        modifiers.remove(modifier.modifierId());
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        modifiers.forEach((id, level) -> nbt.putInt(id.toString(), level));
        return nbt;
    }

    @NonNull
    public static LensModifiersData readNbt(NbtCompound nbt) {
        val modifiers = nbt.getKeys().stream()
                .collect(Collectors.toMap(Identifier::new, nbt::getInt, Integer::max, Object2IntOpenHashMap::new));
        return new LensModifiersData(modifiers);
    }
}

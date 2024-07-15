package ru.feytox.etherology.magic.lens;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.CodecUtil;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class LensModifiersData {

    public static final Codec<LensModifiersData> CODEC = Codec.unboundedMap(Identifier.CODEC, Codec.INT).xmap(LensModifiersData::new, LensModifiersData::getModifiers);
    public static final PacketCodec<ByteBuf, LensModifiersData> PACKET_CODEC = CodecUtil.map(Object2IntOpenHashMap::new, Identifier.PACKET_CODEC, PacketCodecs.VAR_INT).xmap(LensModifiersData::new, LensModifiersData::getModifiers);

    @NonNull
    protected final Map<Identifier, Integer> modifiers;

    public static LensModifiersData empty() {
        return new LensModifiersData(new Object2IntOpenHashMap<>());
    }

    public int getLevel(LensModifier modifier) {
        return modifiers.getOrDefault(modifier.modifierId(), 0);
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

    public Mutable asMutable() {
        return new Mutable(new Object2IntOpenHashMap<>(modifiers));
    }

    public static class Mutable extends LensModifiersData {

        public Mutable(@NonNull Map<Identifier, Integer> modifiers) {
            super(modifiers);
        }

        public void setLevel(LensModifier modifier, int level) {
            if (level == 0) removeModifier(modifier);
            else modifiers.put(modifier.modifierId(), level);
        }

        public void removeModifier(LensModifier modifier) {
            if (!modifiers.containsKey(modifier.modifierId())) return;
            modifiers.remove(modifier.modifierId());
        }
    }
}

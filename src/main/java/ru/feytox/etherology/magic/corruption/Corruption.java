package ru.feytox.etherology.magic.corruption;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.CheckReturnValue;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.registry.misc.EtherologyComponents;


public record Corruption(float corruptionValue) {

    public static final Codec<Corruption> CODEC = Codec.FLOAT.xmap(Corruption::new, Corruption::corruptionValue).stable();
    public static final PacketCodec<RegistryByteBuf, Corruption> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, Corruption::corruptionValue, Corruption::new);

    @Nullable
    public static Corruption ofAspects(AspectContainer aspects) {
        Integer aspectsCount = aspects.sum().orElse(null);
        if (aspectsCount == null || aspectsCount == 0) return null;

        return ofAspects(aspectsCount);
    }

    public static Corruption ofAspects(float aspectsCount) {
        return new Corruption(aspectsCount * 0.1f);
    }

    @Nullable
    @CheckReturnValue
    public Corruption increment(float value) {
        return corruptionValue + value > 0 ? new Corruption(corruptionValue + value) : null;
    }

    public void placeInChunk(ServerWorld world, BlockPos pos) {
        CorruptionComponent component = world.getChunk(pos).getComponent(EtherologyComponents.CORRUPTION);
        component.setCorruption(this.add(component.getCorruption()));
    }

    public boolean isEmpty() {
        return corruptionValue <= 0;
    }

    @Nullable
    @CheckReturnValue
    public Corruption add(@Nullable Corruption corruption) {
        if (corruption == null) return new Corruption(corruptionValue);
        return increment(corruption.corruptionValue);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("corruption_value", corruptionValue);
    }

    @Nullable
    public static Corruption readFromNbt(NbtCompound nbt) {
        float value = nbt.getFloat("corruption_value");
        return value == 0.0f ? null : new Corruption(value);
    }
}

package ru.feytox.etherology.magic.corruption;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.CheckReturnValue;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.registry.util.EtherologyComponents;
import ru.feytox.etherology.util.misc.NbtReadable;


@Getter
@RequiredArgsConstructor
public class Corruption implements NbtReadable<Corruption> {

    private final float corruptionValue;

    @Nullable
    public static Corruption of(AspectContainer aspects) {
        Integer aspectsCount = aspects.sum().orElse(null);
        if (aspectsCount == null || aspectsCount == 0) return null;

        return of(aspectsCount);
    }

    public static Corruption of(float aspectsCount) {
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

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("corruption_value", corruptionValue);
    }

    @Nullable
    public static Corruption readFromNbt(NbtCompound nbt) {
        float value = nbt.getFloat("corruption_value");
        return value == 0.0f ? null : new Corruption(value);
    }

    @Nullable
    @Override
    public Corruption readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }
}

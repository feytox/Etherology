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
import ru.feytox.etherology.util.misc.Nbtable;


@Getter
@RequiredArgsConstructor
public class Corruption implements Nbtable {

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

    @CheckReturnValue
    public Corruption increment(float value) {
        return new Corruption(corruptionValue + value);
    }

    public void placeInChunk(ServerWorld world, BlockPos pos) {
        CorruptionComponent component = world.getChunk(pos).getComponent(EtherologyComponents.CORRUPTION);
        component.setCorruption(this.add(component.getCorruption()));
    }

    public boolean isEmpty() {
        return corruptionValue <= 0;
    }

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
    public Nbtable readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }
}

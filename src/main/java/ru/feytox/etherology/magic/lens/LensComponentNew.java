package ru.feytox.etherology.magic.lens;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.With;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.registry.misc.EComponentTypes;
import ru.feytox.etherology.util.misc.ItemData;

import java.util.Optional;

@With
public record LensComponentNew(int charge, LensMode mode, LensPattern pattern, LensModifiersData modifiers,
                               long endTick, int gameId) {

    public static final Codec<LensComponentNew> CODEC;
    public static final LensComponentNew EMPTY = new LensComponentNew(0, LensMode.STREAM, LensPattern.empty(), LensModifiersData.empty(), -1, Etherology.GAME_ID);

    public long getCooldown(World world) {
        return endTick - world.getTime();
    }

    public int getLevel(LensModifier lensModifier) {
        return modifiers().getLevel(lensModifier);
    }

    /**
     * @param lensModifier The lens modifier, used to calculate the value.
     * @param start The value at level 0.
     * @param end The value at the highest level.
     * @param modifier The modifier from 0 to 1, representing how fast the value grows with level. A smaller number means faster growth.
     * @return The calculated value.
     */
    public float calcValue(LensModifier lensModifier, float start, float end, float modifier) {
        int level = getLevel(lensModifier);
        return (float) (end - (end - start) * Math.pow(modifier, level));
    }

    /**
     * @param lensModifier The lens modifier, used to calculate the value.
     * @param start The value at level 0.
     * @param end The value at the highest level.
     * @param modifier The modifier from 0 to 1, representing how fast the value grows with level. A smaller number means faster growth.
     * @return The calculated value rounded to the nearest integer.
     */
    public int calcRoundValue(LensModifier lensModifier, int start, int end, float modifier) {
        return Math.round(calcValue(lensModifier, start, end, modifier));
    }

    public static Optional<ItemData<LensComponentNew>> getWrapper(ItemStack stack) {
        return get(stack).map(component -> new ItemData<>(stack, EComponentTypes.LENS, component));
    }

    public static Optional<LensComponentNew> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(EComponentTypes.LENS));
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Codec.INT.fieldOf("charge").forGetter(LensComponentNew::charge),
                        LensMode.CODEC.fieldOf("mode").forGetter(LensComponentNew::mode),
                        LensPattern.CODEC.fieldOf("pattern").forGetter(LensComponentNew::pattern),
                        LensModifiersData.CODEC.fieldOf("modifiers").forGetter(LensComponentNew::modifiers),
                        Codec.LONG.fieldOf("end_tick").forGetter(LensComponentNew::endTick),
                        Codec.INT.fieldOf("game_id").forGetter(LensComponentNew::gameId)
                ).apply(instance, LensComponentNew::new));
    }
}

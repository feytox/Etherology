package ru.feytox.etherology.magic.lens;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.With;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.util.misc.ItemData;

import java.util.Optional;

@With
public record LensComponent(int charge, LensMode mode, LensPattern pattern, LensModifiersData modifiers,
                            long endTick, int gameId) {

    public static final Codec<LensComponent> CODEC;
    public static final LensComponent EMPTY = new LensComponent(0, LensMode.STREAM, LensPattern.empty(), LensModifiersData.empty(), -1, Etherology.GAME_ID);

    public LensComponent incrementCooldown(World world, long cooldown) {
        if (checkCooldown(world)) return withEndTick(world.getTime() + cooldown);
        return withEndTick(endTick + cooldown);
    }

    public LensComponent incrementLevel(LensModifier modifier) {
        LensModifiersData.Mutable modifiers = this.modifiers.asMutable();
        modifiers.setLevel(modifier, getLevel(modifier)+1);
        return withModifiers(modifiers);
    }

    public long getCooldown(World world) {
        return endTick - world.getTime();
    }

    public int getLevel(LensModifier lensModifier) {
        return modifiers().getLevel(lensModifier);
    }

    public boolean checkCooldown(World world) {
        return getCooldown(world) <= 0;
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

    public static Optional<ItemData<LensComponent>> getWrapper(ItemStack stack) {
        return get(stack).map(component -> new ItemData<>(stack, ComponentTypes.LENS, component));
    }

    public static Optional<LensComponent> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(ComponentTypes.LENS));
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Codec.INT.fieldOf("charge").forGetter(LensComponent::charge),
                        LensMode.CODEC.fieldOf("mode").forGetter(LensComponent::mode),
                        LensPattern.CODEC.fieldOf("pattern").forGetter(LensComponent::pattern),
                        LensModifiersData.CODEC.fieldOf("modifiers").forGetter(LensComponent::modifiers),
                        Codec.LONG.fieldOf("end_tick").forGetter(LensComponent::endTick),
                        Codec.INT.fieldOf("game_id").forGetter(LensComponent::gameId)
                ).apply(instance, LensComponent::new));
    }
}

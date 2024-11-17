package ru.feytox.etherology.recipes.jewelry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipe;

import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public abstract class AbstractJewelryRecipe implements FeyRecipe<JewelryTableInventory> {

    private final Pattern pattern;
    private final int ether;

    @Override
    public boolean matches(JewelryTableInventory inventory, World world) {
        return recipeMatches(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(JewelryTableInventory input, RegistryWrapper.WrapperLookup lookup) {
        return craft(input);
    }

    public abstract ItemStack craft(JewelryTableInventory input);

    protected boolean recipeMatches(ItemStack lensStack) {
        return LensComponent.get(lensStack)
                .map(component -> component.pattern().equals(getLensPattern())).orElse(false);
    }

    public LensPattern getLensPattern() {
        return pattern.pattern;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    public record Pattern(LensPattern pattern, Optional<List<String>> data) {
        public static final Codec<Pattern> CODEC = Codec.STRING.listOf().flatXmap(Pattern::fromData, pattern -> pattern.data().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe")));
        public static final PacketCodec<ByteBuf, Pattern> PACKET_CODEC = LensPattern.PACKET_CODEC.xmap(pattern -> new Pattern(pattern, Optional.empty()), Pattern::pattern);

        public static Pattern create(List<String> pattern) {
            return fromData(pattern).getOrThrow();
        }

        // TODO: 11.07.2024 add error on wrong pattern
        private static DataResult<Pattern> fromData(List<String> data) {
            String flatPattern = String.join("", data);
            if (flatPattern.length() != 64) throw new IllegalArgumentException("Jewelry Pattern must have 8x8 size");

            IntArraySet cracks = new IntArraySet();
            IntArraySet softCells = new IntArraySet();

            for (int i = 1; i < flatPattern.length(); i++) {
                switch (flatPattern.charAt(i)) {
                    case 'X' -> cracks.add(i);
                    case 'Y' -> softCells.add(i);
                }
            }

            return DataResult.success(new Pattern(new LensPattern(cracks, softCells), Optional.of(data)));
        }
    }
}

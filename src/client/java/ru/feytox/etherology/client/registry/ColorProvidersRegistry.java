package ru.feytox.etherology.client.registry;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.GrassColors;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlock;
import ru.feytox.etherology.client.util.FeyColor;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.misc.RGBColor;

import static ru.feytox.etherology.registry.block.EBlocks.BREWING_CAULDRON;

@UtilityClass
public class ColorProvidersRegistry {

    public static void registerAll() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (tintIndex != 1 || world == null || pos == null) return -1;
            int biomeColor = BiomeColors.getWaterColor(world, pos);
            int aspectsPercent = state.get(BrewingCauldronBlock.ASPECTS_LVL);
            return FeyColor.getGradientColor(RGBColor.of(biomeColor), RGBColor.of(0x8032B5), aspectsPercent / 200f).asHex();
        }, BREWING_CAULDRON);

        ColorProviderRegistry.BLOCK.register(ColorProvidersRegistry::getGrassColor, DecoBlocks.LIGHTELET);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            BlockState blockState = ((BlockItem)stack.getItem()).getBlock().getDefaultState();
            return getGrassColor(blockState, null, null, tintIndex);
        }, DecoBlocks.LIGHTELET);
    }

    private static int getGrassColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        return world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5, 1.0);
    }
}

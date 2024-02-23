package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlock;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

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
    }
}

package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.*;

@UtilityClass
public class BlockRenderLayerMapRegistry {

    public static void registerAll() {
        registerCutout(ETHEREAL_SOCKET);
        registerCutout(PEACH_DOOR);
        registerCutout(PEACH_TRAPDOOR);
        registerCutout(SAMOVAR_BLOCK);
        registerCutout(SPILL_BARREL);
        registerCutout(BEAMER);
        registerCutout(PEACH_SAPLING);
        registerCutout(PEACH_LEAVES);
        registerCutout(ETHEREAL_METRONOME);
        registerCutout(ETHEREAL_SPINNER);
        registerCutout(ETHEREAL_STORAGE);
        registerCutout(BREWING_CAULDRON);
        registerCutout(PEDESTAL_BLOCK);
        registerCutout(POTTED_BEAMER);
        registerCutout(THUJA);
        registerCutout(THUJA_PLANT);
        registerCutout(TUNING_FORK);
    }

    private static void registerCutout(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
    }
}

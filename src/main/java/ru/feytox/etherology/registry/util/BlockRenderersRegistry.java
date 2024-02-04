package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import ru.feytox.etherology.block.armillar.ArmillaryMatrixRenderer;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronRenderer;
import ru.feytox.etherology.block.crate.CrateBlockRenderer;
import ru.feytox.etherology.block.etherealGenerators.metronome.EtherealMetronomeRenderer;
import ru.feytox.etherology.block.etherealGenerators.spinner.EtherealSpinnerRenderer;
import ru.feytox.etherology.block.etherealSocket.EtherealSocketRenderer;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageRenderer;
import ru.feytox.etherology.block.furniture.FurnitureBlockEntityRenderer;
import ru.feytox.etherology.block.jewelryTable.JewelryTableRenderer;
import ru.feytox.etherology.block.pedestal.PedestalRenderer;

import static ru.feytox.etherology.registry.block.DecoBlocks.ETHEROLOGY_SIGN;
import static ru.feytox.etherology.registry.block.EBlocks.*;

@UtilityClass
public class BlockRenderersRegistry {

    public static void registerAll() {
        register(FURNITURE_BLOCK_ENTITY, FurnitureBlockEntityRenderer::new);
        register(ETHEREAL_STORAGE_BLOCK_ENTITY, EtherealStorageRenderer::new);
        register(ETHEREAL_SOCKET_BLOCK_ENTITY, EtherealSocketRenderer::new);
        register(ETHEREAL_SPINNER_BLOCK_ENTITY, EtherealSpinnerRenderer::new);
        register(ETHEREAL_METRONOME_BLOCK_ENTITY, EtherealMetronomeRenderer::new);
        register(ETHEROLOGY_SIGN, SignBlockEntityRenderer::new);
        register(CRATE_BLOCK_ENTITY, CrateBlockRenderer::new);
        register(BREWING_CAULDRON_BLOCK_ENTITY, BrewingCauldronRenderer::new);
        register(PEDESTAL_BLOCK_ENTITY, PedestalRenderer::new);
        register(ARMILLARY_MATRIX_BLOCK_ENTITY, ArmillaryMatrixRenderer::new);
        register(JEWELRY_TABLE_BLOCK_ENTITY, JewelryTableRenderer::new);
    }

    private static <T extends BlockEntity> void register(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererFactory<T> rendererFactory) {
        BlockEntityRendererFactories.register(blockEntityType, rendererFactory);
    }
}

package ru.feytox.etherology.blocks.crate;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CrateBlockRenderer extends GeoBlockRenderer<CrateBlockEntity> {
    public CrateBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new CrateBlockModel());
    }
}

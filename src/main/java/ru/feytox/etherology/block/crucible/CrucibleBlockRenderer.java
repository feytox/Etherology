package ru.feytox.etherology.block.crucible;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CrucibleBlockRenderer extends GeoBlockRenderer<CrucibleBlockEntity> {
    public CrucibleBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new CrucibleBlockModel());
    }
}

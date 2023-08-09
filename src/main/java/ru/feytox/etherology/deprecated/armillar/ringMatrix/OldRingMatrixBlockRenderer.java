package ru.feytox.etherology.deprecated.armillar.ringMatrix;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class OldRingMatrixBlockRenderer extends GeoBlockRenderer<OldRingMatrixBlockEntity> {
    public OldRingMatrixBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new OldRingMatrixBlockModel());
    }
}

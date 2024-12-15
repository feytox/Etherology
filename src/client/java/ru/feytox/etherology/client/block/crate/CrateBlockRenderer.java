package ru.feytox.etherology.client.block.crate;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import ru.feytox.etherology.block.crate.CrateBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CrateBlockRenderer extends GeoBlockRenderer<CrateBlockEntity> {
    public CrateBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new CrateBlockModel());
    }
}

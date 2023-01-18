package name.uwu.feytox.etherology.blocks.ringMatrix;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RingMatrixBlockRenderer extends GeoBlockRenderer<RingMatrixBlockEntity> {
    public RingMatrixBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new RingMatrixBlockModel());
    }
}

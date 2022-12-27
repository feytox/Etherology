package name.uwu.feytox.lotyh.blocks.crucible;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class CrucibleBlockRenderer extends GeoBlockRenderer<CrucibleBlockEntity> {
    public CrucibleBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new CrucibleBlockModel());
    }

    @Override
    public RenderLayer getRenderType(CrucibleBlockEntity animatable, float partialTick, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

}

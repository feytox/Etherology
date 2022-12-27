package name.uwu.feytox.lotyh.blocks.ringMatrix;

import name.uwu.feytox.lotyh.blocks.crucible.CrucibleBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RingMatrixBlockRenderer extends GeoBlockRenderer<RingMatrixBlockEntity> {
    public RingMatrixBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new RingMatrixBlockModel());
    }

    @Override
    public RenderLayer getRenderType(RingMatrixBlockEntity animatable, float partialTick, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}

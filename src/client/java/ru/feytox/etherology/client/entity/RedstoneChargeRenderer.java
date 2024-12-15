package ru.feytox.etherology.client.entity;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import ru.feytox.etherology.entity.RedstoneChargeEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RedstoneChargeRenderer extends GeoEntityRenderer<RedstoneChargeEntity> {

    public RedstoneChargeRenderer(EntityRendererFactory.Context context) {
        super(context, new RedstoneChargeModel());
    }

    @Override
    public void preRender(MatrixStack poseStack, RedstoneChargeEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.translate(0f, 0.25f, 0f);
    }
}

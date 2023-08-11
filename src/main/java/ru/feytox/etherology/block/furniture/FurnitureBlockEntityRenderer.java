package ru.feytox.etherology.block.furniture;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class FurnitureBlockEntityRenderer implements BlockEntityRenderer<FurSlabBlockEntity> {
    private final BlockEntityRendererFactory.Context ctx;

    public FurnitureBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void render(FurSlabBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        FurnitureData bottomData = entity.getBottomData();
        FurnitureData topData = entity.getTopData();
        if (bottomData != null) bottomData.render(ctx, entity, tickDelta, matrices, vertexConsumers, light, overlay);
        if (topData != null) topData.render(ctx, entity, tickDelta, matrices, vertexConsumers, light, overlay);
    }
}

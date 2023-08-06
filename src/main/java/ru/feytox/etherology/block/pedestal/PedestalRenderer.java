package ru.feytox.etherology.block.pedestal;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@RequiredArgsConstructor
public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

    private final BlockEntityRendererFactory.Context ctx;

    @Override
    public void render(PedestalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null) return;
        ItemStack stack = entity.getStack(0);
        if (stack.isEmpty()) return;

        renderPedestalItem(entity, matrices, world, stack, vertexConsumers, tickDelta, light);
    }

    /**
     * @see net.minecraft.client.render.entity.ItemEntityRenderer#render(ItemEntity, float, float, MatrixStack, VertexConsumerProvider, int) 
     */
    private void renderPedestalItem(PedestalBlockEntity entity, MatrixStack matrices, World world, ItemStack itemStack, VertexConsumerProvider vertexConsumers, float tickDelta, int light) {
        ItemRenderer itemRenderer = ctx.getItemRenderer();

        matrices.push();
        matrices.translate(0.5f, 1.0f, 0.5f);
        BakedModel bakedModel = itemRenderer.getModel(itemStack, world, null, 5678);
        boolean hasDepth = bakedModel.hasDepth();
        float yOffset = MathHelper.sin((world.getTime() + tickDelta) / 10.0F + getUniqueOffset(entity)) * 0.1F + 0.1F;
        float deltaYOffset = bakedModel.getTransformation().getTransformation(ModelTransformation.Mode.GROUND).scale.y();
        matrices.translate(0.0F, yOffset + 0.25F * deltaYOffset, 0.0F);
        float yRotation = (world.getTime() + tickDelta) / 20.0F + getUniqueOffset(entity);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yRotation));
        float zScale = bakedModel.getTransformation().ground.scale.z();

        matrices.push();
        itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, bakedModel);
        matrices.pop();
        if (!hasDepth) matrices.translate(0.0F, 0.0F, 0.09375F * zScale);

        matrices.pop();
    }

    private static float getUniqueOffset(PedestalBlockEntity pedestal) {
        if (pedestal.getCachedUniqueOffset() != null) return pedestal.getCachedUniqueOffset();

        BlockPos pos = pedestal.getPos();
        float sum = 0.0f;
        sum += pos.getX() % 32;
        sum += pos.getY() % 64;
        sum += pos.getZ() % 128;
        float uniquePercent = MathHelper.abs(sum) / 100.0f;
        float result = 2 * MathHelper.PI * uniquePercent;
        pedestal.setCachedUniqueOffset(result);
        return result;
    }
}

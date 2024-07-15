package ru.feytox.etherology.block.pedestal;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Math;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.misc.PseudoLivingEntity;
import ru.feytox.etherology.util.misc.UniqueProvider;

@RequiredArgsConstructor
public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

    private final BlockEntityRendererFactory.Context ctx;

    @Override
    public void render(PedestalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null || entity.isRemoved()) return;
        ItemStack stack = entity.getStack(0);
        if (stack.isEmpty()) return;

        renderPedestalItem(entity, matrices, world, stack, vertexConsumers, tickDelta, light, ctx.getItemRenderer(), new Vec3d(0.5, 1.0, 0.5));
    }

    /**
     * @see net.minecraft.client.render.entity.ItemEntityRenderer#render(ItemEntity, float, float, MatrixStack, VertexConsumerProvider, int) 
     */
    public static <T extends BlockEntity & UniqueProvider> void renderPedestalItem(T entity, MatrixStack matrices, World world, ItemStack itemStack, VertexConsumerProvider vertexConsumers, float tickDelta, int light, ItemRenderer itemRenderer, Vec3d offset) {
        float uniqueOffset = entity.getUniqueOffset(entity.getPos());

        renderVanillaGroundItem(matrices, world, itemStack, vertexConsumers, tickDelta, light, itemRenderer, offset, uniqueOffset, entity.getPos());
    }

    public static void renderVanillaGroundItem(MatrixStack matrices, World world, ItemStack itemStack, VertexConsumerProvider vertexConsumers, float tickDelta, int light, ItemRenderer itemRenderer, Vec3d offset, float uniqueOffset, BlockPos blockPos) {
        matrices.push();
        matrices.translate(offset.x, offset.y, offset.z);
        // TODO: 17.06.2024 consider replacing with something BETTER
        LivingEntity pseudoEntity = itemStack.isOf(ToolItems.WARP_COUNTER) ? new PseudoLivingEntity(world, blockPos) : null;
        BakedModel bakedModel = itemRenderer.getModel(itemStack, world, pseudoEntity, 5678);
        boolean hasDepth = bakedModel.hasDepth();
        long time = world.getTime();
        float yOffset = Math.sin((time + tickDelta) / 10.0F + uniqueOffset) * 0.1F + 0.1F;
        float deltaYOffset = bakedModel.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
        matrices.translate(0.0F, yOffset + 0.25F * deltaYOffset, 0.0F);
        float yRotation = (time + tickDelta) / 20.0F + uniqueOffset;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yRotation));
        float zScale = bakedModel.getTransformation().ground.scale.z();

        matrices.push();
        itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, bakedModel);
        matrices.pop();
        if (!hasDepth) matrices.translate(0.0F, 0.0F, 0.09375F * zScale);

        matrices.pop();
    }
}

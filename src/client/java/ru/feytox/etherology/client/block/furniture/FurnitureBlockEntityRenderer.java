package ru.feytox.etherology.client.block.furniture;

import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.block.furniture.FurSlabBlockEntity;
import ru.feytox.etherology.block.furniture.FurnitureData;
import ru.feytox.etherology.block.shelf.ShelfData;

public class FurnitureBlockEntityRenderer implements BlockEntityRenderer<FurSlabBlockEntity> {
    private final BlockEntityRendererFactory.Context ctx;

    public FurnitureBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void render(FurSlabBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var bottomData = entity.getBottomData();
        var topData = entity.getTopData();
        if (bottomData != null)
            renderData(bottomData, ctx, entity, tickDelta, matrices, vertexConsumers, light, overlay);
        if (topData != null)
            renderData(topData, ctx, entity, tickDelta, matrices, vertexConsumers, light, overlay);
    }

    private void renderData(FurnitureData furnitureData, BlockEntityRendererFactory.Context ctx, FurSlabBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (furnitureData instanceof ShelfData data)
            renderShelf(data, ctx, entity, tickDelta, matrices, vertexConsumers, light, overlay);
    }

    private void renderShelf(ShelfData data, BlockEntityRendererFactory.Context ctx, FurSlabBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null) return;

        var itemRenderer = ctx.getItemRenderer();
        ItemStack leftStack = data.getStack(0);
        ItemStack rightStack = data.getStack(1);

        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);
        light = WorldRenderer.getLightmapCoordinates(world, entity.getPos().add(facing.getVector()));
        float degrees = 180 - facing.asRotation();
        float rads = degrees * MathHelper.PI / 180;

        // нахождение "уголка"
        Vec3d northPoint = new Vec3d(-0.5, 0, -0.5);
        Vec3d diffVec = northPoint.rotateY(rads).subtract(northPoint);

        double y = data.isBottom() ? 0.25 : 0.75;
        Vec3d leftItemVec = new Vec3d(0.75, y, 0.05).rotateY(rads).add(diffVec);
        Vec3d rightItemVec = new Vec3d(0.25, y, 0.05).rotateY(rads).add(diffVec);

        matrices.push();
        matrices.translate(leftItemVec.x, leftItemVec.y, leftItemVec.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees));
        matrices.scale(0.25f, 0.25f, 0.25f);
        itemRenderer.renderItem(leftStack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, world, 621);
        matrices.pop();

        matrices.push();
        matrices.translate(rightItemVec.x, rightItemVec.y, rightItemVec.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees));
        matrices.scale(0.25f, 0.25f, 0.25f);
        itemRenderer.renderItem(rightStack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, world, 621);
        matrices.pop();
    }
}

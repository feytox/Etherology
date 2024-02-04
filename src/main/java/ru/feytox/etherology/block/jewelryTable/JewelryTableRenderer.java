package ru.feytox.etherology.block.jewelryTable;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.block.pedestal.PedestalRenderer;

@RequiredArgsConstructor
public class JewelryTableRenderer implements BlockEntityRenderer<JewelryBlockEntity> {

    private final BlockEntityRendererFactory.Context ctx;

    @Override
    public void render(JewelryBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null || entity.isRemoved()) return;
        ItemStack stack = entity.getStack(0);
        if (stack.isEmpty()) return;

        light = WorldRenderer.getLightmapCoordinates(world, entity.getPos().up());

        PedestalRenderer.renderPedestalItem(entity, matrices, world, stack, vertexConsumers, tickDelta, light, ctx.getItemRenderer(), new Vec3d(0.5, 1.0, 0.5));
    }
}

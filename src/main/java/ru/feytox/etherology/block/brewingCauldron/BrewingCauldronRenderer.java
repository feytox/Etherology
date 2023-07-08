package ru.feytox.etherology.block.brewingCauldron;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static net.minecraft.client.render.model.json.ModelTransformation.Mode.FIXED;

public class BrewingCauldronRenderer extends GeoBlockRenderer<BrewingCauldronBlockEntity> {
    private final BlockEntityRendererFactory.Context ctx;

    public BrewingCauldronRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new BrewingCauldronModel());
        this.ctx = ctx;
    }

    @Override
    public void defaultRender(MatrixStack matrices, BrewingCauldronBlockEntity cauldron, VertexConsumerProvider vertexConsumers, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer, float yaw, float tickDelta, int light) {
        int overlay = getPackedOverlay(animatable, 0);
        renderItems(matrices, cauldron, vertexConsumers, tickDelta, light, overlay);

        super.defaultRender(matrices, cauldron, vertexConsumers, renderType, buffer, yaw, tickDelta, light);
    }

    private void renderItems(MatrixStack matrices, BrewingCauldronBlockEntity cauldron, VertexConsumerProvider vertexConsumers, float tickDelta, int light, int overlay) {
        ItemRenderer itemRenderer = ctx.getItemRenderer();
        World world = cauldron.getWorld();
        if (world == null) return;

        DefaultedList<ItemStack> items = cauldron.getItems();
        int itemCount = cauldron.getLastStackSlot() + 1;
        float deltaAngle = (2 * MathHelper.PI) / itemCount;
        float timeAngle = ((world.getTime() + tickDelta) / 15f) % (2 * MathHelper.PI);

        BlockState state = cauldron.getCachedState();
        int waterLevel = state.get(BrewingCauldronBlock.LEVEL);
        Vec3d centerPoint = new Vec3d(0.5, 0.4475 + 0.0625 * (waterLevel - 1), 0.5);

        for (int i = 0; i < itemCount; i++) {
            ItemStack itemStack = items.get(i);
            float angle = deltaAngle * i + timeAngle;
            double dx = MathHelper.cos(angle) * 0.225d;
            double dz = MathHelper.sin(angle) * 0.225d;

            matrices.push();
            matrices.translate(centerPoint.x + dx, centerPoint.y, centerPoint.z + dz);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            matrices.scale(0.15f, 0.15f, 0.15f);
            itemRenderer.renderItem(itemStack, FIXED, light, overlay, matrices, vertexConsumers, 621);
            matrices.pop();
        }
    }
}

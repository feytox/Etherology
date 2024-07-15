package ru.feytox.etherology.block.armillary;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.pedestal.PedestalRenderer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ArmillaryMatrixRenderer extends GeoBlockRenderer<ArmillaryMatrixBlockEntity> {

    private final BlockEntityRendererFactory.Context ctx;

    public ArmillaryMatrixRenderer(BlockEntityRendererFactory.Context context) {
        super(new ArmillaryMatrixModel());
        ctx = context;
    }

    @Override
    public void defaultRender(MatrixStack poseStack, ArmillaryMatrixBlockEntity animatable, VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        World world = animatable.getWorld();
        if (world != null) {
            Vec3d offset = new Vec3d(0.5, 2.3, 0.5);
            ItemStack stack = animatable.getStack(0);
            PedestalRenderer.renderPedestalItem(animatable, poseStack, world, stack, bufferSource, partialTick, packedLight, ctx.getItemRenderer(), offset);
        }

        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
    }
}

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
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.subtypes.LightSubtype;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static net.minecraft.client.render.model.json.ModelTransformation.Mode.FIXED;
import static ru.feytox.etherology.registry.particle.ServerParticleTypes.LIGHT;

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
        int lightParticlesCount = cauldron.checkCacheItems();
        int itemCount = cauldron.getLastStackSlot() + 1;

        BlockState state = cauldron.getCachedState();
        Vec3d centerPoint = BrewingCauldronBlockEntity.getWaterPos(state);
        Vec3d cauldronPos = Vec3d.of(cauldron.getPos()).add(centerPoint);

        boolean notRenderItem = itemCount == 0;
        int steps = notRenderItem && lightParticlesCount != 0 ? lightParticlesCount : itemCount;
        float deltaAngle = (2 * MathHelper.PI) / steps;
        float timeAngle = ((world.getTime() + tickDelta) / 15f) % (2 * MathHelper.PI);

        for (int i = 0; i < steps; i++) {
            ItemStack itemStack = items.get(i);
            float angle = deltaAngle * i + timeAngle;
            double dx = MathHelper.cos(angle) * 0.225d;
            double dz = MathHelper.sin(angle) * 0.225d;

            if (notRenderItem) spawnLightParticles(world, cauldronPos, dx, dz);
            else renderItem(matrices, vertexConsumers, light, overlay, itemRenderer, centerPoint, itemStack, dx, dz);
        }
        cauldron.cacheItems();
    }

    private void spawnLightParticles(World world, Vec3d cauldronPos, double dx, double dz) {
        LightParticleEffect effect = new LightParticleEffect(LIGHT, LightSubtype.BREWING, new Vec3d(0, 0, 0));
        Vec3d particlePos = cauldronPos.add(dx, 0.05, dz);
        effect.spawnParticles(world, 1, 0, 0, 0, particlePos);
    }

    private void renderItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ItemRenderer itemRenderer, Vec3d centerPoint, ItemStack itemStack, double dx, double dz) {
        matrices.push();
        matrices.translate(centerPoint.x + dx, centerPoint.y, centerPoint.z + dz);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.scale(0.15f, 0.15f, 0.15f);
        itemRenderer.renderItem(itemStack, FIXED, light, overlay, matrices, vertexConsumers, 621);
        matrices.pop();
    }
}

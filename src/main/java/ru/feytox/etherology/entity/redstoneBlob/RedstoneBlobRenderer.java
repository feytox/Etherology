package ru.feytox.etherology.entity.redstoneBlob;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Supplier;

public class RedstoneBlobRenderer extends EntityRenderer<RedstoneBlob> {
    static final Supplier<List<Vec3d>> PARTICLES_POSES = Suppliers.memoize(() -> generateSpherePoses(0.25f, 0.75f));

    public RedstoneBlobRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    private static List<Vec3d> generateSpherePoses(float radius, float stepAngle) {
        List<Vec3d> points = new ObjectArrayList<>();
        for (float phi = 0; phi <= MathHelper.PI; phi += stepAngle) {
            for (float theta = 0; theta <= 2 * MathHelper.PI; theta += stepAngle) {
                double x = radius * MathHelper.sin(phi) * MathHelper.cos(theta);
                double y = radius * MathHelper.sin(phi) * MathHelper.sin(theta);
                double z = radius * MathHelper.cos(phi);
                points.add(new Vec3d(x, y, z));
            }
        }
        return points;
    }

    @Override
    public void render(RedstoneBlob entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        if (entity.world == null) return;

        List<Vec3d> poses = RedstoneBlobRenderer.PARTICLES_POSES.get();
        Vec3d blobPos = entity.getLerpedPos(tickDelta).add(0, entity.getHeight() / 2, 0);

        poses.forEach(pos -> entity.world.addParticle(new DustParticleEffect(DustParticleEffect.RED, 0.5f), blobPos.x+pos.x, blobPos.y+pos.y, blobPos.z+pos.z, 0, 0, 0));
    }

    @Override
    public Identifier getTexture(RedstoneBlob entity) {
        return new Identifier("air");
    }
}

package ru.feytox.etherology.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.particle.utility.FeyParticle;

public class RisingParticle extends FeyParticle<SimpleParticleEffect> {
    public RisingParticle(ClientWorld clientWorld, double x, double y, double z, SimpleParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        age = random.nextBetween(0, 5);
        maxAge = random.nextBetween(20, 40);
        scale(2.5f + 2.0f * random.nextFloat());

        setSpriteForAge();
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        RenderSystem.depthMask(false);

        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

        Float zRotation = this.angle == 0.0f ? null : MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float i = this.getSize(tickDelta);

        for(int j = 0; j < 4; ++j) {
            Vector3f vector3f = vector3fs[j];
            if (zRotation != null) vector3f.rotateZ(zRotation);
            vector3f.rotateY(camera.getYaw() * -0.017453292F);
            vector3f.mul(i);
            vector3f.add(f, g, h);
        }

        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(tickDelta);
        vertexConsumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(o);
        vertexConsumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).texture(l, m).color(this.red, this.green, this.blue, this.alpha).light(o);
        vertexConsumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).texture(k, m).color(this.red, this.green, this.blue, this.alpha).light(o);
        vertexConsumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).texture(k, n).color(this.red, this.green, this.blue, this.alpha).light(o);
    }
}

package ru.feytox.etherology.particle.utility;

import lombok.NonNull;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public abstract class VertexParticle extends SpriteBillboardParticle {
    @NonNull
    private final Vec3d leftBottom;
    @NonNull
    private final Vec3d leftTop;
    @NonNull
    private final Vec3d rightTop;
    @NonNull
    private final Vec3d rightBottom;

    protected VertexParticle(ClientWorld clientWorld, @NonNull Vec3d leftBottom, @NonNull Vec3d leftTop, @NonNull Vec3d rightTop, @NonNull Vec3d rightBottom) {
        super(clientWorld, leftBottom.x, leftBottom.y, leftBottom.z, 0, 0, 0);
        this.leftBottom = leftBottom;
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.rightBottom = rightBottom;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        Vec3d lBottom = this.leftBottom.subtract(cameraPos);
        Vec3d lTop = this.leftTop.subtract(cameraPos);
        Vec3d rTop = this.rightTop.subtract(cameraPos);
        Vec3d rBottom = this.rightBottom.subtract(cameraPos);

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int light = this.getBrightness(tickDelta);

        // Вершины для передней стороны частицы
        vertexConsumer.vertex(lBottom.x, lBottom.y, lBottom.z).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(lTop.x, lTop.y, lTop.z).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(rTop.x, rTop.y, rTop.z).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(rBottom.x, rBottom.y, rBottom.z).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light).next();

        // Вершины для задней стороны частицы
        vertexConsumer.vertex(rBottom.x, rBottom.y, rBottom.z).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(rTop.x, rTop.y, rTop.z).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(lTop.x, lTop.y, lTop.z).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(lBottom.x, lBottom.y, lBottom.z).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
    }
}

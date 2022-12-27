package name.uwu.feytox.lotyh.util;

// штучки, которые я переделал, а то в owo lib фигово сделаны

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.Drawer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class UwuLib {

    public static Surface tiled(Identifier texture, int u1, int v1, int u2, int v2, int textureWidth, int textureHeight) {
        return (matrices, component) -> {
            RenderSystem.setShaderTexture(0, texture);
            Drawer.drawTexture(matrices, component.x(), component.y(), u1, v1, u2-u1+1, v2-v1+1, textureWidth, textureHeight);
        };
    }

    public static ButtonComponent.Renderer texture(Identifier texture, int u1, int v1, int u2, int v2, int textureWidth, int textureHeight) {
        return (matrices, button, delta) -> {
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, texture);
            Drawer.drawTexture(matrices, button.x, button.y, u1, v1, u2-u1+1, v2-v1+1, textureWidth, textureHeight);
        };
    }

    public static ButtonComponent.Renderer bigTexture(Identifier texture, int u1, int v1, int u2, int v2, int textureWidth, int textureHeight) {
        return (matrices, button, delta) -> {
            int renderV1 = v1;
            int renderV2 = v2;
            // TODO: прочекать на более простой вариант
            if (!button.isHovered()) {
                renderV1 += 200;
                renderV2 += 200;
            }
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, texture);
            Drawer.drawTexture(matrices, button.x, button.y, u1, renderV1, u2 - u1 + 1, renderV2 - renderV1 + 1, textureWidth, textureHeight);
        };
    }

    public static void drawParticleLine(ServerWorld world, Vec3d pos1, Vec3d pos2, DefaultParticleType particle, float step) {
        double vx = pos2.x - pos1.x;
        double vy = pos2.y - pos1.y;
        double vz = pos2.z - pos1.z;
        Vec3d vec = new Vec3d(vx, vy, vz);

        Vec3d stepVec = vec.multiply(step / vec.length());

        for (int i = 1; i <= MathHelper.ceil(vec.length() / step); i++) {
            Vec3d particlePos = stepVec.multiply(i);
            world.spawnParticles(particle, pos1.x + particlePos.x, pos1.y + particlePos.y, pos1.z + particlePos.z,
                    3, 0, 0, 0, 0);
        }
    }

    public static void drawParticleLine(ServerWorld world, Entity entity1, Entity entity2, DefaultParticleType particle, float step) {
        drawParticleLine(world, getCenterPos(entity1), getCenterPos(entity2), particle, step);
    }

    public static void drawParticleLine(ServerWorld world, Vec3d pos1, Entity entity2, DefaultParticleType particle, float step) {
        drawParticleLine(world, pos1, getCenterPos(entity2), particle, step);
    }

    public static Vec3d getCenterPos(Entity entity) {
        return new Vec3d(entity.getX() + entity.getWidth() * 0.5,
                entity.getY() + entity.getHeight() * 0.5,
                entity.getZ() + entity.getWidth() * 0.5);
    }
}

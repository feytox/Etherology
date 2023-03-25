package ru.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.EVec3d;
import ru.feytox.etherology.util.feyapi.RGBColor;

import java.util.List;

import static ru.feytox.etherology.Etherology.GLINT_PARTICLE;

public class GlintParticle extends SpriteBillboardParticle {
    private Vec3d currentVec;
    private double endX;
    private double endY;
    private double endZ;
    private boolean secondComplete = false;

    public GlintParticle(ClientWorld clientWorld, double x0, double y0, double z0, double x1, double y1, double z1) {
        super(clientWorld, x0, y0, z0, x1, y1, z1);
        updateVec(x0, y0, z0, x1, y1, z1);
        this.scale(0.1f);
        setRGB(new RGBColor(244, 194, 133));
        this.maxAge = 60 + this.random.nextInt(6);
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        if (age < 10) {
            acceleratedMoveOnVec(0.1f, 0.5f, false, false);
        } else if (age < 30) {
            currentVec = currentVec
                    .rotateX((0.5f - random.nextFloat()) * MathHelper.PI / 6)
                    .rotateY((0.5f - random.nextFloat()) * MathHelper.PI / 6)
                    .rotateZ((0.5f - random.nextFloat()) * MathHelper.PI / 6);
            acceleratedMoveOnVec(0.2f, 0.4f, false, false);
        } else if (age == 30) {
            currentVec = currentVec.multiply(0.01);
            secondComplete = true;
        }
        if (secondComplete) {
            acceleratedMoveOnVec(0.2f, 0.3f, false, true);
        }
    }

    private void acceleratedMoveOnVec(float speed_k, float start_speed, boolean deadOnEnd, boolean inverted) {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;

        Vec3d remPath = deadOnEnd ? new Vec3d(endX-x, endY-y, endZ-z) : currentVec;
        double remPathLen = remPath.length();
        double fullPathLen = currentVec.length();

        if (remPathLen <= 0.5f && deadOnEnd) {
            this.markDead();
            return;
        }

        double progress = inverted ? remPathLen / fullPathLen : 1 - remPathLen / fullPathLen;
        double deltaC = speed_k * Math.pow(progress + start_speed, 3);
        Vec3d deltaVec = remPath.multiply(Math.min(deltaC / remPathLen, 1));
        x += deltaVec.x;
        y += deltaVec.y;
        z += deltaVec.z;
    }

    private void updateVec(double x0, double y0, double z0, double x1, double y1, double z1) {
        currentVec = new Vec3d(x1-x0, y1-y0, z1-z0);
        endX = x1;
        endY = y1;
        endZ = z1;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void setRGB(double red, double green, double blue) {
        super.setColor((float) (red / 255d), (float) (green / 255d), (float) (blue / 255d));
    }

    public void setRGB(RGBColor rgbColor) {
        setRGB(rgbColor.r(), rgbColor.g(), rgbColor.b());
    }

    public static void spawnParticles(ClientWorld world, BlockPos blockPos, Direction facing, float percent) {
        Random random = world.getRandom();
        Vec3d pos = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Vec3d centerPos = blockPos.toCenterPos();

        double dxz = 6/16d;
        double dy = 0.25;
        // down direction
        Vec3d start = new Vec3d(dxz, dy, dxz);
        Vec3d end = new Vec3d(0.25 + dxz, 0.5 + dy, 0.25 + dxz);
        switch (facing) {
            case UP -> {
                start = new Vec3d(dxz, 0.5 + dy, dxz);
                end = new Vec3d(0.25 + dxz, dy, 0.25 + dxz);
            }
            case NORTH -> {
                start = new Vec3d(dxz, dxz, dy);
                end = new Vec3d(0.25 + dxz, 0.25 + dxz, 0.5 + dy);
            }
            case SOUTH -> {
                start = new Vec3d(dxz, dxz, 0.5 + dy);
                end = new Vec3d(0.25 + dxz, 0.25 + dxz, dy);
            }
            case WEST -> {
                start = new Vec3d(dy, dxz, dxz);
                end = new Vec3d(0.5 + dy, 0.25 + dxz, 0.25 + dxz);
            }
            case EAST -> {
                start = new Vec3d(0.5 + dy, dxz, dxz);
                end = new Vec3d(dy, 0.25 + dxz, 0.25 + dxz);
            }
        }

        List<Vec3d> particlePoses = EVec3d.boxOf(pos.add(start), pos.add(end), 0.05d, facing);

        int count = MathHelper.floor(50f * percent);
        for (int i = 0; i < count; i++) {
            int j = random.nextInt(particlePoses.size()-1);
            Vec3d particlePos = particlePoses.get(j);
            Vec3d path = particlePos.subtract(centerPos);
            path = particlePos.add(path.multiply(1 + random.nextDouble() * 5));
            world.addParticle(GLINT_PARTICLE, particlePos.x, particlePos.y, particlePos.z, path.x, path.y, path.z);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            GlintParticle particle = new GlintParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}

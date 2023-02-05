package name.uwu.feytox.etherology.particle;

import name.uwu.feytox.etherology.util.feyapi.FVec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class LightVitalParticle extends MovingParticle {
    private double passedWay = 0;
    private final FVec3d fullPath;
    private final int wavesType;
    private final SpriteProvider spriteProvider;
    private int lastSpriteId = -1;

    public LightVitalParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
                              SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);

        Random random = Random.create();
        float randFloat = random.nextFloat();
        this.scale(0.05f + randFloat * 0.25f);
        this.alpha *= 0.78f * randFloat;

        this.fullPath = new FVec3d(endX - startX, endY - startY, endZ - startZ);

        this.spriteProvider = spriteProvider;
        this.nextSprite();
        this.setSprite(spriteProvider);

//        this.wavesType = randWaves < 2 ? randWaves - 2 : randWaves - 1;
//        this.wavesType = randWaves < 1 ? randWaves - 1 : randWaves;
        this.wavesType = 1;
        this.maxAge = 500;
    }

    @Override
    public void tick() {
        // путь от частицы до центра (для проверки ниже)
        double wayLen = new Vec3d(endX-x, endY-y, endZ-z).length();

        if (this.age++ >= this.maxAge || wayLen <= 0.5f) {
            this.markDead();
            return;
        }

        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;

        // длина вектора прямого пути от начала до конца
        double fullLen = fullPath.length();
        double speed = 0.04 * (1 + wayLen / fullLen);

        // значение функции
        double z = (wavesType < 0 ? -1 : 1)
                * 0.2 * MathHelper.sin((float) (passedWay * (4 - MathHelper.abs(wavesType)) * MathHelper.PI / fullLen));

        // двумерный вектор с x - аргументом, а z - значением функции
        Vec3d vecXZ = new Vec3d(passedWay, 0, z);
        double vecXZLen = vecXZ.length();

        // вектор длины вектора vecXZ, сонаправленный с вектором прямого пути
        Vec3d stepVec = fullPath.multiply(vecXZLen / fullLen);
        // вектор выше поворачивается на угол между OX и vecXZ
        Vec3d vecXYZ = stepVec.rotateY((z >= 0 ? 1 : -1) * (float) (Math.acos(passedWay / vecXZLen)));

        // конвертация векторов в координатную форму
        Vec3d coords = vecXYZ.add(startX, startY, startZ);

        this.x = coords.x;
        this.y = coords.y;
        this.z = coords.z;

        this.passedWay += speed;
        this.nextSprite();
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }


    private void nextSprite() {
        int SPRITES_NUM = 9;

        lastSpriteId += 1;
        lastSpriteId = lastSpriteId >= SPRITES_NUM ? 0 : lastSpriteId;
        this.setSprite(spriteProvider.getSprite(lastSpriteId, SPRITES_NUM));

        // 140 - g
        this.setRGB(25, 218 - (78 * Math.abs(lastSpriteId - 5) / 5f), 190);
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
            return new LightVitalParticle(world, x, y, z, velocityX, velocityY, velocityZ,
                    this.spriteProvider);
        }
    }
}

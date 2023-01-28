package name.uwu.feytox.etherology.particle;

import name.uwu.feytox.etherology.util.FVec3d;
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
    private static int lastParticleSprite = -1;
    private double passedWay = 0;
    private final FVec3d fullPath;
    private final int wavesType;
    private final float angleX;
    private final float angleY;
    private final float angleZ;
    private final float vecYaw;
    private final SpriteProvider spriteProvider;
    private final double spiralDX;
    private final double spiralDY;
    private int lastSpriteId = -1;

    public LightVitalParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
                              SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);

        Random random = Random.create();
        float randFloat = random.nextFloat();
        this.scale(0.05f + randFloat * 0.25f);
        this.alpha *= 0.78f * randFloat;

        this.fullPath = new FVec3d(endX - startX, endY - startY, endZ - startZ);

        // определение угла альфа для формулы, мною выведенной
        double alphaAngle = MathHelper.HALF_PI -
                Math.acos(MathHelper.sqrt((float) (fullPath.x * fullPath.x + fullPath.z * fullPath.z)) / fullPath.length());
        double a = 0.3 * MathHelper.sqrt(2 * (1 - MathHelper.cos((float) alphaAngle)));
        this.spiralDX = MathHelper.cos((float) (0.5 * alphaAngle));
        this.spiralDY = MathHelper.sin((float) (0.5 * alphaAngle));

        this.angleX = fullPath.getAngleX();
        this.angleY = fullPath.getAngleY();
        this.angleZ = fullPath.getAngleZ();

        this.vecYaw = (float) MathHelper.atan2(fullPath.x, fullPath.z);

        this.spriteProvider = spriteProvider;
//        this.nextSprite();
        this.nextParticleSprite();

        int randWaves = random.nextBetween(0, 1);
//        this.wavesType = randWaves < 2 ? randWaves - 2 : randWaves - 1;
        this.wavesType = randWaves < 1 ? randWaves - 1 : randWaves;

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
        double speed = 0.04;

        // вектор длины вектора vecXZ, сонаправленный с вектором прямого пути
        Vec3d stepVec = fullPath.multiply(passedWay / fullLen);

        // определение угла поворота плоскости, где первый множитель - количество оборотов (в радианах)
        float DNAangle = (float) (4 * 2 * MathHelper.PI * (wayLen / fullLen)) % 2 * MathHelper.PI;
        DNAangle = wavesType < 0 ? 2 * MathHelper.PI - DNAangle : DNAangle;
        // поворот на угол выше (в радианах)
        Vec3d diffVec = new Vec3d(MathHelper.sin(DNAangle) * 0.2 * 0.3, MathHelper.cos(DNAangle) * 0.2, 0);
        diffVec = diffVec.rotateZ(MathHelper.HALF_PI / 2 - vecYaw);
        diffVec = diffVec.rotateY(-angleY);
        stepVec = stepVec.add(diffVec);


        // конвертация векторов в координатную форму
        Vec3d coords = stepVec.add(startX, startY, startZ);

        this.x = coords.x;
        this.y = coords.y;
        this.z = coords.z;

        this.passedWay += speed;
//        this.nextSprite();
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    private void nextParticleSprite() {
        int SPRITES_NUM = 9;

        lastParticleSprite += 1;
        lastParticleSprite = lastParticleSprite >= SPRITES_NUM ? 0 : lastParticleSprite;

        this.setSprite(spriteProvider.getSprite(lastParticleSprite, SPRITES_NUM));
        this.setRGB(25, 218 - (78 * Math.abs(lastParticleSprite - 5) / 5f), 190);
    }

//    private void nextSprite() {
//        int SPRITES_NUM = 9;
//
//        lastSpriteId += 1;
//        lastSpriteId = lastSpriteId >= SPRITES_NUM ? 0 : lastSpriteId;
//        this.setSprite(spriteProvider.getSprite(lastSpriteId, SPRITES_NUM));
//
//        // 140 - g
//        this.setRGB(25, 218 - (78 * Math.abs(lastSpriteId - 5) / 5f), 190);
//    }

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

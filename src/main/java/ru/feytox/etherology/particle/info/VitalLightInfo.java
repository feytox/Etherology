package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class VitalLightInfo extends ParticleInfo<LightParticle, LightParticleEffect> {
    private double passedWay = 0;
    private final Vec3d endPos;
    private Vec3d fullPath;
    private double inverseFullLen;
    private int lastSpriteId = -1;

    public VitalLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
    }

    @Override
    public void extraInit(LightParticle particle) {
        nextSprite(particle);
        particle.setSprite(particle.getSpriteProvider());

        float rand = particle.getRandom().nextFloat();
        particle.scale(0.05f + rand * 0.25f);
        float alpha = particle.getAlpha() * 0.78f * rand;
        particle.setAlpha(alpha);

        fullPath = endPos.subtract(particle.getStartPos());
        inverseFullLen = particle.getInverseLen(fullPath);
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return null;
    }

    @Override
    public void tick(LightParticle particle) {
        double pathLen = particle.getPathVec(endPos).length();
        if (particle.checkDeadPos(true, pathLen)) return;
        particle.markPrevPos();

        double speed = 0.04f * (1 + pathLen * inverseFullLen);

        // значение функции
        double zValue = 0.2f * MathHelper.sin((float) (passedWay * 3f * MathHelper.PI * inverseFullLen));
        // двумерный вектор с x - аргументом, а z - значением функции
        double vecXZLen = new Vec3d(passedWay, 0, zValue).length();
        // вектор длины вектора vecXZ, сонаправленный с вектором прямого пути
        Vec3d stepVec = fullPath.multiply(vecXZLen * inverseFullLen);
        // вектор выше поворачивается на угол между OX и vecXZ
        Vec3d vecXYZ = stepVec.rotateY((zValue >= 0 ? 1 : -1) * (float) (Math.acos(passedWay / vecXZLen)));
        // конвертация векторов в координаты
        Vec3d newPos = vecXYZ.add(particle.getStartPos());

        particle.updatePos(newPos);
        passedWay += speed;
        nextSprite(particle);
    }

    @Override
    public int getMaxAge(Random random) {
        return 500;
    }

    private void nextSprite(LightParticle particle) {
        lastSpriteId += 1;
        lastSpriteId = lastSpriteId >= 9 ? 0 : lastSpriteId;
        particle.setSprite(particle.getSpriteProvider().getSprite(lastSpriteId, 9));

        // 140 - g
        particle.setRGB(25, 218 - (78 * Math.abs(lastSpriteId - 5) / 5f), 190);
    }
}

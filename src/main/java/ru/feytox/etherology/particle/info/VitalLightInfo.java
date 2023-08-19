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
    private Vec3d currentVec;
    private final Vec3d endPos;
    private int lastSpriteId = -1;

    public VitalLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
    }

    @Override
    public void extraInit(LightParticle particle) {
        nextSprite(particle);
        particle.setSprite(particle.getSpriteProvider());

        Random random = particle.getRandom();
        float rand = random.nextFloat();
        particle.scale(0.05f + rand * 0.25f);
        float alpha = particle.getAlpha() * 0.78f * rand;
        particle.setAlpha(alpha);

        Vec3d fullPath = endPos.subtract(particle.getStartPos());
        currentVec = fullPath.rotateX(MathHelper.PI * (-0.3333f + 0.6666f * random.nextFloat()))
                .rotateY(MathHelper.PI * (-0.3333f + 0.6666f * random.nextFloat()))
                .rotateZ(MathHelper.PI * (-0.3333f + 0.6666f * random.nextFloat()));
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return null;
    }

    @Override
    public void tick(LightParticle particle) {
        if (particle.tickAge()) return;
        Vec3d toEndVec = particle.getPathVec(endPos);
        double inverseToEndLen = particle.getInverseLen(toEndVec);
        if (particle.inverseCheckDeadPos(true, inverseToEndLen)) return;

        Vec3d stepVec = currentVec.multiply(0.025d);
        particle.markPrevPos();
        particle.modifyPos(stepVec);
        currentVec = currentVec.lerp(toEndVec, 0.2d * particle.getRandom().nextDouble());
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

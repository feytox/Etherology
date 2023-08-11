package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class SparkParticle extends MovingParticle<SparkParticleEffect> {
    private final ParticleInfo<SparkParticle, SparkParticleEffect> particleInfo;
    private final Vec3d endPos;

    public SparkParticle(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        particleInfo = buildFromInfo(parameters.getZoneType(), this, clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
        if (particleInfo != null) return;

        maxAge = 80;
        age = random.nextBetween(0, 70);
        setRGB(RGBColor.of(0xf4c285));
        setSpriteForAge();
    }

    @Override
    public void tick() {
        if (tickFromInfo(particleInfo, this)) return;
        acceleratedMovingTick(0.4f, 0.5f, true, endPos);
        setSpriteForAge();

        double passedLen = getPassedVec().length();
        double invFullPathLen = getInverseLen(getFullPathVec(endPos));
        double percent = passedLen * invFullPathLen;
        setRGB(FeyColor.lerp(RGBColor.of(0xf4c285), RGBColor.of(0x530eff), (float) percent));
    }
}

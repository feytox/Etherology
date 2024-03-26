package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class LightSparkInfo extends ParticleInfo<LightParticle, LightParticleEffect> {
    private final Vec3d endPos;

    public LightSparkInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
    }

    @Override
    public float getScale(Random random) {
        return 0.1f;
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return null;
    }

    @Override
    public void tick(LightParticle particle) {
        particle.acceleratedMovingTick(0.4f, 0.5f, true, endPos);
        particle.setSpriteForAge();

        double passedLen = particle.getPassedVec().length();
        double invFullPathLen = particle.getInverseLen(particle.getFullPathVec(endPos));
        double percent = passedLen * invFullPathLen;
        particle.setRGB(FeyColor.lerp(RGBColor.of(0xffffff), RGBColor.of(0x530eff), (float) percent));
    }

    @Override
    public int getMaxAge(Random random) {
        return 80;
    }
}

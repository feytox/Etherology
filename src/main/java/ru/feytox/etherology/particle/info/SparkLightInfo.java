package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class SparkLightInfo extends ParticleInfo<LightParticle, LightParticleEffect> {
    private final Vec3d endPos;
    private final int startRed;
    private final int startGreen;
    private final int startBlue;

    public SparkLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
        startRed = 255;
        startGreen = 255;
        startBlue = 255;
    }

    @Override
    public void extraInit(LightParticle particle) {
        particle.setSprite(particle.getSpriteProvider());
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
        double pathLen = particle.getPathVec(endPos).length();
        double fullPathLen = particle.getFullPathVec(endPos).length();
        particle.setRGB(startRed + (83 - startRed) * ((fullPathLen - pathLen) / fullPathLen),
                startGreen + (14 - startGreen) * ((fullPathLen - pathLen) / fullPathLen),
                startBlue + (255 - startBlue) * ((fullPathLen - pathLen) / fullPathLen));
    }

    @Override
    public int getMaxAge(Random random) {
        return 80;
    }
}

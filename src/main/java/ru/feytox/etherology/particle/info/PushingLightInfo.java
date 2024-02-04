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

public class PushingLightInfo extends ParticleInfo<LightParticle, LightParticleEffect> {
    protected Vec3d endPos;

    public PushingLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
    }

    @Override
    public void extraInit(LightParticle particle) {
        endPos = particle.getStartPos().add(particle.getParameters().getMoveVec());
    }

    @Override
    public float getScale(Random random) {
        return 0.3f;
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return FeyColor.getRandomColor(RGBColor.of(0xA0FF55), RGBColor.of(0x71ED3D), random);
    }

    @Override
    public void tick(LightParticle particle) {
        particle.simpleMovingTick(0.025f, endPos, false);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 40;
    }
}

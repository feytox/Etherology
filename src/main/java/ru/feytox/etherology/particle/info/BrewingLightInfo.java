package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class BrewingLightInfo extends ParticleInfo<LightParticle, LightParticleEffect> {

    public BrewingLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
    }

    @Override
    public void extraInit(LightParticle particle) {
        particle.setSpriteForAge();
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return FeyColor.getRandomColor(RGBColor.of(0xB668FF), RGBColor.of(0xEC49D9), random);
    }

    @Override
    public float getScale(Random random) {
        return 0.3f;
    }

    @Override
    public void tick(LightParticle particle) {
        if (particle.tickAge()) return;
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 20;
    }
}

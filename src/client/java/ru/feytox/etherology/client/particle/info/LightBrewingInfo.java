package ru.feytox.etherology.client.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.client.particle.LightParticle;
import ru.feytox.etherology.client.particle.utility.ParticleInfo;
import ru.feytox.etherology.client.util.FeyColor;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.util.misc.RGBColor;

public class LightBrewingInfo extends ParticleInfo<LightParticle, LightParticleEffect> {

    public LightBrewingInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
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

        int fakeAge = MathHelper.floor((particle.getMaxAge() + particle.getAge()) * 0.5);
        particle.setSprite(particle.getSpriteProvider().getSprite(fakeAge, particle.getMaxAge()));
    }

    @Override
    public int getMaxAge(Random random) {
        return 20;
    }
}

package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.types.SparkParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.particle.utility.ParticleInfo;

public class SparkParticle extends MovingParticle<SparkParticleEffect> {
    private final ParticleInfo<SparkParticle, SparkParticleEffect> particleInfo;

    public SparkParticle(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        particleInfo = buildFromInfo(parameters.getZoneType(), this, clientWorld, x, y, z, parameters, spriteProvider);
        setSpriteForAge();
    }

    @Override
    public void tick() {
        tickFromInfo(particleInfo, this);
    }
}

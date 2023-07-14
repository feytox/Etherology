package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.particle.utility.ParticleInfo;

public class LightParticle extends MovingParticle<LightParticleEffect> {

    private final ParticleInfo<LightParticle, LightParticleEffect> particleInfo;

    public LightParticle(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        particleInfo = buildFromInfo(parameters.getLightType(), this, clientWorld, x, y, z, parameters, spriteProvider);
    }

    @Override
    public void tick() {
        tickFromInfo(particleInfo, this);
    }
}

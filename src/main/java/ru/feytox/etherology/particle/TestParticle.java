package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.types.TestParticleEffect;
import ru.feytox.etherology.particle.utility.FeyParticle;

@Deprecated
public class TestParticle extends FeyParticle<TestParticleEffect> {
    public TestParticle(ClientWorld clientWorld, double x, double y, double z, TestParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
    }
}

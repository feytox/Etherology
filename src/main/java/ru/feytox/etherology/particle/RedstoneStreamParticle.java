package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;

/**
 * Reimplementation of vanilla redstone particle
 * @see net.minecraft.client.particle.RedDustParticle
 */
public class RedstoneStreamParticle extends MovingParticle<SimpleParticleEffect> {

    private final Vec3d moveVec;

    public RedstoneStreamParticle(ClientWorld clientWorld, double x, double y, double z, SimpleParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        float colorMultiplier = random.nextFloat() * 0.4F + 0.6F;
        red = darken(1.0f, colorMultiplier);
        green = darken(0.0f, colorMultiplier);
        blue = darken(0.0f, colorMultiplier);
        int i = (int)(8.0 / (random.nextDouble() * 0.8 + 0.2));
        maxAge = (int) Math.max(i, 1.0F);

        moveVec = new Vec3d(random.nextDouble()*2-1, random.nextDouble()*2-1, random.nextDouble()*2-1);

        setSpriteForAge();
    }

    private float darken(float colorComponent, float multiplier) {
        return (random.nextFloat() * 0.2F + 0.8F) * colorComponent * multiplier;
    }

    @Override
    public void tick() {
        simpleMovingTickInDirection(0.02f, moveVec);
        setSpriteForAge();
    }
}

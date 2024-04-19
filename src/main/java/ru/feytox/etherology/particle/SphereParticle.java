package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;

public class SphereParticle extends MovingParticle<MovingParticleEffect> {

    private final Vec3d moveVec;

    public SphereParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        moveVec = parameters.getMoveVec();
        maxAge = 5;
        setSpriteForAge();
    }

    @Override
    public void tick() {
        simpleMovingTickInDirection(0.25f, moveVec);
        setSpriteForAge();
    }
}

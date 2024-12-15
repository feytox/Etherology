package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.client.particle.utility.MovingParticle;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;

public class HazeParticle extends MovingParticle<SimpleParticleEffect> {

    private final Vec3d moveVec;

    public HazeParticle(ClientWorld clientWorld, double x, double y, double z, SimpleParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        maxAge = random.nextBetween(15, 23);
        scale(3f);
        moveVec = new Vec3d(random.nextDouble()*4-2, random.nextDouble()*10+10, random.nextDouble()*4-2)
                .multiply(1/250f);

        setSpriteForAge();
    }

    @Override
    public void tick() {
        movingTick(moveVec);
        setSpriteForAge();
    }
}

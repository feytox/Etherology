package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.client.particle.utility.MovingParticle;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;

public class AlchemyParticle extends MovingParticle<SimpleParticleEffect> {

    private Vec3d moveVec;

    public AlchemyParticle(ClientWorld clientWorld, double x, double y, double z, SimpleParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        moveVec = new Vec3d(random.nextDouble()*14-7, random.nextDouble()*20+10, random.nextDouble()*14-7)
                .multiply(1/250f);
        maxAge = random.nextBetween(7, 15);

        setSpriteForAge();
    }

    @Override
    public void tick() {
        moveVec = moveVec.add(0, -0.0075, 0);
        movingTick(moveVec);
        setSpriteForAge();
    }
}

package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;

public abstract class EtherParticle extends MovingParticle<MovingParticleEffect> {

    private Vec3d moveVec;

    private EtherParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        Vec3d vec = parameters.getMoveVec();
        double len = vec.length();
        moveVec = vec.multiply(random.nextDouble()*25+25).multiply(1/250f)
                .rotateX((float) ((len-vec.x) * (random.nextFloat()*40-20) * MathHelper.RADIANS_PER_DEGREE))
                .rotateY((float) ((len-vec.y) * (random.nextFloat()*40-20) * MathHelper.RADIANS_PER_DEGREE))
                .rotateZ((float) ((len-vec.z) * (random.nextFloat()*40-20) * MathHelper.RADIANS_PER_DEGREE));

        scale(random.nextFloat()*0.25f+0.6f);
    }

    @Override
    public void tick() {
        movingTick(moveVec);
        moveVec = moveVec.multiply(0.8);

        setSpriteForAge();
    }

    public static class EtherStarParticle extends EtherParticle {
        public EtherStarParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
            super(clientWorld, x, y, z, parameters, spriteProvider);
            maxAge = random.nextBetween(15, 23);
            setSpriteForAge();
        }
    }

    public static class EtherDotParticle extends EtherParticle {
        public EtherDotParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
            super(clientWorld, x, y, z, parameters, spriteProvider);
            maxAge = random.nextBetween(6, 15);
            setSpriteForAge();
        }
    }
}

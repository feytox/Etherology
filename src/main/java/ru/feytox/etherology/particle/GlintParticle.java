package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.util.misc.RGBColor;

public class GlintParticle extends MovingParticle<MovingParticleEffect> {

    private Vec3d currentVec;
    private final int firstAge;
    private int currentSprite;

    public GlintParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        currentVec = parameters.getMoveVec().subtract(startPos);

        scale(0.1f);
        maxAge = random.nextBetween(25, 40);
        firstAge = MathHelper.floor(0.25f * maxAge * random.nextFloat());
        setRandomColor(RGBColor.of(0xFFFFA8), RGBColor.of(0xF1AE75));

        currentSprite = 9 * (40 - maxAge) / 40;
        setSpriteForIndex(currentSprite, 9);
    }

    @Override
    public void tick() {
        if (tickAge()) return;

        if (age % 4 == 0) {
            currentSprite += 1;
            setSpriteForIndex(currentSprite, 9);
        }

        if (age < firstAge) {
            tickMovement(0.1f, 0.6f, false);
            return;
        }
        if (age < maxAge / 2) {
            randomRotateVec(MathHelper.PI / 7);
            tickMovement(0.05f, 0.6f, false);
            return;
        }
        if (age == maxAge / 2) {
            currentVec = currentVec.multiply(0.09);
        }

        randomRotateVec(MathHelper.PI / 9);
        tickMovement(0.04f, 0.4f, true);
    }

    /**
     * @see MovingParticle#acceleratedMovingTick(float, float, boolean, Vec3d)
     */
    public void tickMovement(float speed_k, float start_speed, boolean inverted) {
        markPrevPos();

        double invPathLen = getInverseLen(currentVec);

        double acceleration = inverted ? 1 : 0;
        double delta = speed_k * Math.pow(acceleration + start_speed, 3);

        Vec3d deltaVec = currentVec.multiply(Math.min(delta * invPathLen, 1));
        modifyPos(deltaVec);
    }

    private void randomRotateVec(float radians) {
        currentVec = currentVec.rotateX((0.5f - random.nextFloat()) * radians).rotateY((0.5f - random.nextFloat()) * radians).rotateZ((0.5f - random.nextFloat()) * radians);
    }
}

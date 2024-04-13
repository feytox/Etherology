package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.util.misc.FeyColor;
import ru.feytox.etherology.util.misc.RGBColor;

public class VitalParticle extends MovingParticle<MovingParticleEffect> {

    private final Vec3d endPos;
    private final double inversedfullPathLen;
    private Vec3d currentVec;
    private float colorPercent;
    private boolean isColorAscending;

    public VitalParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
        colorPercent = random.nextFloat();
        isColorAscending = random.nextBoolean();
        maxAge = 500;
        scale(0.5f);
        updateColor();

        Vec3d fullPath = getFullPathVec(endPos);
        inversedfullPathLen = getInverseLen(fullPath);
        currentVec = fullPath.rotateX(MathHelper.PI * (-0.3333f + 0.6666f * random.nextFloat()))
                .rotateY(MathHelper.PI * (-0.3333f + 0.6666f * random.nextFloat()))
                .rotateZ(MathHelper.PI * (-0.3333f + 0.6666f * random.nextFloat()));

        setSprite(spriteProvider);
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        Vec3d toEndVec = getPathVec(endPos);
        double inverseToEndLen = getInverseLen(toEndVec);
        if (inverseCheckDeadPos(true, inverseToEndLen, 0.66d)) return;

        double speedMultiplier = inverseToEndLen / inversedfullPathLen;
        double speed = 0.025d + speedMultiplier * 0.6;

        double inverseCurrentLen = getInverseLen(currentVec);
        Vec3d stepVec = currentVec.multiply(inverseCurrentLen * speed * 0.15);
        markPrevPos();
        modifyPos(stepVec);
        currentVec = currentVec.lerp(toEndVec, 0.2d * random.nextDouble());
        updateColorPercent();
        updateColor();
    }

    private void updateColorPercent() {
        float delta = 0.1f * (isColorAscending ? 1 : -1);
        colorPercent += delta;
        if (colorPercent >= 0.0f && colorPercent <= 1.0f) return;

        float limit = isColorAscending ? 1.0f : 0.0f;
        colorPercent = 2 * limit - colorPercent;
        isColorAscending = !isColorAscending;
    }

    private void updateColor() {
        setRGB(FeyColor.getGradientColor(RGBColor.of(0x00FFFF), RGBColor.of(0x0078FF), colorPercent));
    }
}

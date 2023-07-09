package ru.feytox.etherology.particle.utility;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

public abstract class FeyParticle<T extends FeyParticleEffect<T>> extends SpriteBillboardParticle {
    protected final T parameters;
    protected final SpriteProvider spriteProvider;
    protected Vec3d startPos;

    public FeyParticle(ClientWorld clientWorld, double x, double y, double z, T parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, 0, 0, 0);
        this.parameters = parameters;
        this.spriteProvider = spriteProvider;
        this.startPos = new Vec3d(x, y, z);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    protected double getInverseLen(Vec3d vec) {
        return MathHelper.fastInverseSqrt(vec.lengthSquared());
    }

    protected void modifyPos(Vec3d deltaVec) {
        x += deltaVec.x;
        y += deltaVec.y;
        z += deltaVec.z;
    }

    protected boolean inverseCheckDeadPos(boolean deadOnEnd, double inverseLen) {
        if (!deadOnEnd || inverseLen < 2.0d) return false;
        this.markDead();
        return true;
    }

    protected boolean checkDeadPos(boolean deadOnEnd, double pathLen) {
        if (!deadOnEnd || pathLen > 0.5d) return false;
        this.markDead();
        return true;
    }

    protected boolean tickAge() {
        if (this.age++ < this.maxAge) return false;
        this.markDead();
        return true;
    }

    public void setRGB(double red, double green, double blue) {
        super.setColor((float) (red / 255d), (float) (green / 255d), (float) (blue / 255d));
    }

    public void setRGB(RGBColor rgbColor) {
        setRGB(rgbColor.r(), rgbColor.g(), rgbColor.b());
    }

    public void setRandomColor(RGBColor start, RGBColor end) {
        RGBColor color = FeyColor.getRandomColor(start, end, random);
        setRGB(color);
    }

    public void setAngle(float degrees) {
        angle = (float) (degrees * Math.PI / 180f);
        if (angle >= 2 * Math.PI) angle = (float) (angle - 2 * Math.PI);
    }

    public void modifyAngle(float deltaDegrees) {
        prevAngle = angle;
        angle += (float) (deltaDegrees * Math.PI / 180f);
        if (angle >= 2 * Math.PI) angle = (float) (angle - 2 * Math.PI);
    }
}

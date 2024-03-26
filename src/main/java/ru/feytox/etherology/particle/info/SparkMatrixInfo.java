package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.SparkParticle;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class SparkMatrixInfo  extends ParticleInfo<SparkParticle, SparkParticleEffect> {

    private final Vec3d moveVec;

    public SparkMatrixInfo(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        moveVec = parameters.getMoveVec();
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return RGBColor.of(0xf8e800);
    }

    @Override
    public float getScale(Random random) {
        return 0.75f;
    }

    @Override
    public void tick(SparkParticle particle) {
        particle.simpleMovingTickOnVec(0.15f, moveVec);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 10;
    }
}

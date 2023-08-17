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

public class SparkRisingInfo extends ParticleInfo<SparkParticle, SparkParticleEffect> {
    private final Vec3d endPos;
    private float acceleration = 0.0f;
    private float startSpeed = 0.0f;

    public SparkRisingInfo(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        Vec3d startPos = new Vec3d(x, y, z);
        endPos = startPos.add(parameters.getMoveVec());
    }

    @Override
    public float getScale(Random random) {
        return 0.2f + 0.3f * random.nextFloat();
    }

    @Override
    public void extraInit(SparkParticle particle) {
        particle.setSpriteForAge();
        acceleration = 0.005f * particle.getRandom().nextFloat();
        startSpeed = 0.001f * particle.getRandom().nextFloat();
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return null;
    }

    @Override
    public void tick(SparkParticle particle) {
        if (particle.tickAge()) return;
        particle.timeAcceleratedMovingTick(acceleration, startSpeed, endPos, false);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return random.nextBetween(10, 40);
    }
}

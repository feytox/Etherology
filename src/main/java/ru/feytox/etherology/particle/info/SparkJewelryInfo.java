package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import ru.feytox.etherology.particle.SparkParticle;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.RGBColor;

import static ru.feytox.etherology.particle.SparkParticle.SPRITES_COUNT;

public class SparkJewelryInfo extends ParticleInfo<SparkParticle, SparkParticleEffect> {

    private final Vec3d endPos;

    public SparkJewelryInfo(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        Vec3d startPos = new Vec3d(x, y, z);
        endPos = startPos.add(parameters.getMoveVec());
    }

    @Override
    public void extraInit(SparkParticle particle) {
        int age = Math.round(particle.getMaxAge() * (float) particle.getRandom().nextBetween(0, 2) / SPRITES_COUNT);
        particle.setAge(age);
        super.extraInit(particle);
    }

    @Override
    public float getScale(Random random) {
        return 0.8f + 0.3f * random.nextFloat();
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return RGBColor.of(0xFFBFF6);
    }

    @Override
    public void tick(SparkParticle particle) {
        if (particle.tickAge()) return;
        particle.simpleMovingTick(0.0075f, endPos, false);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 30;
    }
}

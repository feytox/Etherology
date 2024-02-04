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

public class SparkJewelryInfo extends ParticleInfo<SparkParticle, SparkParticleEffect> {

    private final Vec3d endPos;

    public SparkJewelryInfo(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        Vec3d startPos = new Vec3d(x, y, z);
        endPos = startPos.add(parameters.getMoveVec());
    }

    @Override
    public float getScale(Random random) {
        return 0.35f + 0.3f * random.nextFloat();
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return RGBColor.of(0xffd700);
    }

    @Override
    public void tick(SparkParticle particle) {
        if (particle.tickAge()) return;
        particle.simpleMovingTick(0.015f, endPos, false);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 80 + random.nextBetween(0, 20);
    }
}

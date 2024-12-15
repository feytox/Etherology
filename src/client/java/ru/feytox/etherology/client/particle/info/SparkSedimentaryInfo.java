package ru.feytox.etherology.client.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.client.particle.SparkParticle;
import ru.feytox.etherology.client.particle.utility.ParticleInfo;
import ru.feytox.etherology.client.util.FeyColor;
import ru.feytox.etherology.magic.seal.SealType;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.util.misc.RGBColor;

public class SparkSedimentaryInfo extends ParticleInfo<SparkParticle, SparkParticleEffect> {
    private final Vec3d endPos;
    private final RGBColor startColor;
    private final RGBColor endColor;

    public SparkSedimentaryInfo(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider, RGBColor startColor, RGBColor endColor) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public static ParticleInfo.Factory<SparkParticle, SparkParticleEffect> of(SealType sealType) {
        return (clientWorld, x, y, z, parameters, spriteProvider) -> new SparkSedimentaryInfo(clientWorld, x, y, z, parameters, spriteProvider, sealType.getStartColor(), sealType.getEndColor());
    }

    @Override
    public void extraInit(SparkParticle particle) {
        super.extraInit(particle);
        particle.setAge(particle.getRandom().nextBetween(0, 10));
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return startColor == null || endColor == null ? null : FeyColor.getRandomColor(startColor, endColor, random);
    }

    @Override
    public void tick(SparkParticle particle) {
        particle.acceleratedMovingTick(0.01f, 0.1f, false, endPos);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 20;
    }
}

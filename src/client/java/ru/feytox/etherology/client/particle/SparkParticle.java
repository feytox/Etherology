package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.client.particle.info.SparkJewelryInfo;
import ru.feytox.etherology.client.particle.info.SparkRisingInfo;
import ru.feytox.etherology.client.particle.info.SparkSedimentaryInfo;
import ru.feytox.etherology.client.particle.utility.MovingParticle;
import ru.feytox.etherology.client.particle.utility.ParticleInfo;
import ru.feytox.etherology.client.particle.utility.ParticleInfoProvider;
import ru.feytox.etherology.client.util.FeyColor;
import ru.feytox.etherology.magic.seal.SealType;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.subtype.SparkSubtype;
import ru.feytox.etherology.util.misc.RGBColor;

public class SparkParticle extends MovingParticle<SparkParticleEffect> implements ParticleInfoProvider<SparkSubtype, SparkParticle, SparkParticleEffect> {
    // TODO: 08.02.2024 un-hardcode
    public static final int SPRITES_COUNT = 6;
    private final ParticleInfo<SparkParticle, SparkParticleEffect> particleInfo;
    private final Vec3d endPos;

    public SparkParticle(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        particleInfo = buildFromInfo(parameters.getSparkType(), this, clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
        if (particleInfo != null) return;

        maxAge = 80;
        age = random.nextBetween(0, 70);
        setRGB(RGBColor.of(0xf4c285));
        setSpriteForAge();
    }

    @Override
    public void tick() {
        if (tickFromInfo(particleInfo, this)) return;
        acceleratedMovingTick(0.4f, 0.5f, true, endPos);
        setSpriteForAge();

        double passedLen = getPassedVec().length();
        double invFullPathLen = getInverseLen(getFullPathVec(endPos));
        double percent = passedLen * invFullPathLen;
        setRGB(FeyColor.lerp(RGBColor.of(0xf4c285), RGBColor.of(0x530eff), (float) percent));
    }

    @Override
    public ParticleInfo.@Nullable Factory<SparkParticle, SparkParticleEffect> getFactory(SparkSubtype subtype) {
        return switch (subtype) {
            case SIMPLE -> null;
            case KETA -> SparkSedimentaryInfo.of(SealType.KETA);
            case RELLA -> SparkSedimentaryInfo.of(SealType.RELLA);
            case VIA -> SparkSedimentaryInfo.of(SealType.VIA);
            case CLOS -> SparkSedimentaryInfo.of(SealType.CLOS);
            case RISING -> SparkRisingInfo::new;
            case JEWELRY -> SparkJewelryInfo::new;
        };
    }
}

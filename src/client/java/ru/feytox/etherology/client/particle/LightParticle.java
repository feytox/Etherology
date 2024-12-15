package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.client.particle.info.*;
import ru.feytox.etherology.client.particle.utility.MovingParticle;
import ru.feytox.etherology.client.particle.utility.ParticleInfo;
import ru.feytox.etherology.client.particle.utility.ParticleInfoProvider;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.subtype.LightSubtype;

public class LightParticle extends MovingParticle<LightParticleEffect> implements ParticleInfoProvider<LightSubtype, LightParticle, LightParticleEffect> {

    private final ParticleInfo<LightParticle, LightParticleEffect> particleInfo;

    public LightParticle(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        particleInfo = buildFromInfo(parameters.getLightType(), this, clientWorld, x, y, z, parameters, spriteProvider);
    }

    @Override
    public void tick() {
        tickFromInfo(particleInfo, this);
    }

    @Override
    public ParticleInfo.Factory<LightParticle, LightParticleEffect> getFactory(LightSubtype subtype) {
        return switch (subtype) {
            case SIMPLE -> LightSimpleInfo::new;
            case SPARK -> LightSparkInfo::new;
            case PUSHING -> LightPushingInfo::new;
            case ATTRACT -> LightAttractInfo::new;
            case BREWING -> LightBrewingInfo::new;
            case MATRIX -> LightMatrixInfo::new;
            case GENERATOR -> LightGeneratorInfo::new;
            case HAZE -> LightHazeInfo::new;
        };
    }
}

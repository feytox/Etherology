package ru.feytox.etherology.registry.particle;

import lombok.experimental.UtilityClass;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.types.MovingParticleEffect;
import ru.feytox.etherology.particle.types.SparkParticleEffect;
import ru.feytox.etherology.particle.types.ZoneParticleEffect;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.types.misc.FeyParticleType;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class ServerParticleTypes {
    public static final FeyParticleType<LightParticleEffect> LIGHT = register("light", LightParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> THUNDER_ZAP = register("thunder_zap", MovingParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> STEAM = register("steam", MovingParticleEffect::new);
    public static final FeyParticleType<ZoneParticleEffect> ZONE_PARTICLE = register("zone_particle", ZoneParticleEffect::new);
    public static final FeyParticleType<SparkParticleEffect> SPARK = register("spark_new", SparkParticleEffect::new);

    private static <T extends ParticleEffect> FeyParticleType<T> register(String name, FeyParticleEffect.DummyConstructor<T> dummyConstructor) {
        FeyParticleType<T> particleType = new FeyParticleType<>(false, dummyConstructor);
        return Registry.register(Registries.PARTICLE_TYPE, new EIdentifier(name), particleType);
    }
}

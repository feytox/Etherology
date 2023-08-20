package ru.feytox.etherology.registry.particle;

import lombok.experimental.UtilityClass;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.particle.effects.*;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.effects.misc.FeyParticleType;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class ServerParticleTypes {
    public static final FeyParticleType<LightParticleEffect> LIGHT = register("light", LightParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> THUNDER_ZAP = register("thunder_zap", MovingParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> STEAM = register("steam", MovingParticleEffect::new);
    public static final FeyParticleType<ZoneParticleEffect> ZONE_PARTICLE = register("zone_particle", ZoneParticleEffect::new);
    public static final FeyParticleType<SparkParticleEffect> SPARK = register("spark", SparkParticleEffect::new);
    public static final FeyParticleType<ElectricityParticleEffect> ELECTRICITY1 = register("electricity1", ElectricityParticleEffect::new);
    public static final FeyParticleType<ElectricityParticleEffect> ELECTRICITY2 = register("electricity2", ElectricityParticleEffect::new);
    public static final FeyParticleType<ItemParticleEffect> ITEM = register("item", ItemParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> RISING = register("rising", SimpleParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> VITAL = register("vital", MovingParticleEffect::new);

    private static <T extends ParticleEffect> FeyParticleType<T> register(String name, FeyParticleEffect.DummyConstructor<T> dummyConstructor) {
        FeyParticleType<T> particleType = new FeyParticleType<>(false, dummyConstructor);
        return Registry.register(Registries.PARTICLE_TYPE, new EIdentifier(name), particleType);
    }

    private static <T extends Enum<T>> FeyParticleType<TypedParticleEffect<T>> registerTyped(String name, Class<T> enumClass) {
        return register(name, type -> new TypedParticleEffect<>(type, enumClass));
    }
}

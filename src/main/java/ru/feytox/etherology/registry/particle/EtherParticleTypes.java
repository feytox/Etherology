package ru.feytox.etherology.registry.particle;

import lombok.experimental.UtilityClass;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.particle.effects.*;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.effects.misc.FeyParticleType;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class EtherParticleTypes {
    public static final FeyParticleType<LightParticleEffect> LIGHT = register("light", LightParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> STEAM = register("steam", SimpleParticleEffect::new);
    public static final FeyParticleType<SparkParticleEffect> SPARK = register("spark", SparkParticleEffect::new);
    public static final FeyParticleType<ElectricityParticleEffect> ELECTRICITY1 = register("electricity1", ElectricityParticleEffect::new);
    public static final FeyParticleType<ElectricityParticleEffect> ELECTRICITY2 = register("electricity2", ElectricityParticleEffect::new);
    public static final FeyParticleType<ItemParticleEffect> ITEM = register("item", ItemParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> RISING = register("rising", SimpleParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> VITAL = register("vital", MovingParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> SHOCKWAVE = register("shockwave", SimpleParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> GLINT = register("glint_particle", MovingParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> ENERGY_ABSORPTION = register("energy_absorption", SimpleParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> ARMILLARY_SPHERE = register("armillary_sphere", MovingParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> HAZE = register("haze", SimpleParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> ALCHEMY = register("alchemy", SimpleParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> ETHER_STAR = register("ether_star", MovingParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> ETHER_DOT = register("ether_dot", MovingParticleEffect::new);
    public static final FeyParticleType<ScalableParticleEffect> RESONATION = register("resonation", ScalableParticleEffect::new);
    public static final FeyParticleType<ScalableParticleEffect> LIGHTNING_BOLT = register("lightning_bolt", ScalableParticleEffect::new);
    public static final FeyParticleType<ScalableParticleEffect> SCALABLE_SWEEP = register("scalable_sweep", ScalableParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> REDSTONE_FLASH = register("redstone_flash", SimpleParticleEffect::new);
    public static final FeyParticleType<SimpleParticleEffect> REDSTONE_STREAM = register("redstone_stream", SimpleParticleEffect::new);

    private static <T extends ParticleEffect> FeyParticleType<T> register(String name, FeyParticleEffect.DummyConstructor<T> dummyConstructor) {
        FeyParticleType<T> particleType = new FeyParticleType<>(false, dummyConstructor);
        return Registry.register(Registries.PARTICLE_TYPE, EIdentifier.of(name), particleType);
    }
}

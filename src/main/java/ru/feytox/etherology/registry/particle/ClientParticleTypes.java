package ru.feytox.etherology.registry.particle;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import ru.feytox.etherology.particle.*;
import ru.feytox.etherology.particle.utility.FactoryProvider;

import static ru.feytox.etherology.registry.particle.ServerParticleTypes.*;

@Environment(EnvType.CLIENT)
@UtilityClass
public class ClientParticleTypes {
    public static void registerAll() {
        register(LIGHT, LightParticle::new);
        register(STEAM, SteamParticle::new);
        register(ZONE_PARTICLE, ZoneParticle::new);
        register(SPARK, SparkParticle::new);
        register(ELECTRICITY1, ElectricityParticle::new);
        register(ELECTRICITY2, ElectricityParticle::new);
        register(ITEM, ItemParticle::new);
        register(RISING, RisingParticle::new);
        register(VITAL, VitalParticle::new);
        register(SHOCKWAVE, ShockwaveParticle::new);
        register(GLINT, GlintParticle::new);
        register(ENERGY_ABSORPTION, EnergyAbsorptionParticle::new);
        register(ARMILLARY_SPHERE, SphereParticle::new);
        register(HAZE, HazeParticle::new);
        register(ALCHEMY, AlchemyParticle::new);
        register(ETHER_STAR, EtherParticle.EtherStarParticle::new);
        register(ETHER_DOT, EtherParticle.EtherDotParticle::new);
        register(RESONATION, ResonationParticle::new);
        register(LIGHTNING_BOLT, LightningBoltParticle::new);
    }

    private static <T extends ParticleEffect, P extends Particle> void register(ParticleType<T> particleType, FactoryProvider.ParticleConstructor<T, P> particleConstructor) {
        var factory = FactoryProvider.createFactory(particleConstructor);
        ParticleFactoryRegistry.getInstance().register(particleType, factory);
    }
}

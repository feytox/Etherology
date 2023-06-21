package ru.feytox.etherology.registry.particle;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import ru.feytox.etherology.particle.TestParticle;
import ru.feytox.etherology.particle.utility.FactoryProvider;

import static ru.feytox.etherology.registry.particle.ServerParticleTypes.TEST2;

@Environment(EnvType.CLIENT)
@UtilityClass
public class ClientParticleTypes {
    public static void registerAll() {
        register(TEST2, TestParticle::new);
    }

    private static <T extends ParticleEffect, P extends Particle> void register(ParticleType<T> particleType, FactoryProvider.ParticleConstructor<T, P> particleConstructor) {
        var factory = FactoryProvider.createFactory(particleConstructor);
        ParticleFactoryRegistry.getInstance().register(particleType, factory);
    }
}

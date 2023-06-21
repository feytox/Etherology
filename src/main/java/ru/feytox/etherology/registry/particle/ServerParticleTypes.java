package ru.feytox.etherology.registry.particle;

import lombok.experimental.UtilityClass;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.particle.types.TestParticleEffect;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.types.misc.FeyParticleType;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class ServerParticleTypes {
    public static final ParticleType<TestParticleEffect> TEST2 = register("test2", false, TestParticleEffect::new);

    private static <T extends ParticleEffect> ParticleType<T> register(String name, boolean alwaysShow, FeyParticleEffect.DummyConstructor<T> dummyConstructor) {
        FeyParticleType<T> particleType = new FeyParticleType<>(alwaysShow, dummyConstructor);
        return Registry.register(Registries.PARTICLE_TYPE, new EIdentifier(name), particleType);
    }
}

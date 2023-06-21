package ru.feytox.etherology.particle.utility;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;

@UtilityClass
public class FactoryProvider {
    public static <T extends ParticleEffect, P extends Particle> ParticleFactoryRegistry.PendingParticleFactory<T> createFactory(ParticleConstructor<T, P> particleConstructor) {
        return (spriteProvider) ->
                (ParticleFactory<T>) (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        particleConstructor.create(world, x, y, z, parameters, spriteProvider);
    }

    @FunctionalInterface
    public interface ParticleConstructor<T extends ParticleEffect, P extends Particle> {
        P create(ClientWorld clientWorld, double x, double y, double z, T parameters, SpriteProvider spriteProvider);
    }
}

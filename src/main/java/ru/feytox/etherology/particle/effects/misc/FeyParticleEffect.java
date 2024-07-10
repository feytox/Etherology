package ru.feytox.etherology.particle.effects.misc;

import com.mojang.serialization.MapCodec;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class FeyParticleEffect<T extends ParticleEffect> implements ParticleEffect {

    @Getter
    protected final ParticleType<T> type;

    public FeyParticleEffect(ParticleType<T> type) {
        this.type = type;
    }

    public abstract MapCodec<T> createCodec();
    public abstract PacketCodec<RegistryByteBuf, T> createPacketCodec();

    public <V> Function<V, T> factory(BiFunction<ParticleType<T>, V, T> biFactory) {
        return value -> biFactory.apply(type, value);
    }

    public <V, M> BiFunction<V, M, T> biFactory(TriFunction<ParticleType<T>, V, M, T> triFactory) {
        return (value1, value2) -> triFactory.apply(type, value1, value2);
    }

    public void spawnParticles(World world, int count, double delta, Vec3d centerPos) {
        spawnParticles(this, world, count, delta, centerPos);
    }

    public void spawnParticles(World world, int count, double deltaX, double deltaY, double deltaZ, Vec3d centerPos) {
        spawnParticles(this, world, count, deltaX, deltaY, deltaZ, centerPos);
    }

    public static void spawnParticles(ParticleEffect effect, World world, int count, double delta, Vec3d centerPos) {
        spawnParticles(effect, world, count, delta, delta, delta, centerPos);
    }

    public static void spawnParticles(ParticleEffect effect, World world, int count, double deltaX, double deltaY, double deltaZ, Vec3d centerPos) {
        Random random = world.getRandom();
        for (int i = 0; i < count; i++) {
            Vec3d start = centerPos.add(getRandomPos(random, deltaX, deltaY, deltaZ));

            if (world.isClient) world.addParticle(effect, start.x, start.y, start.z, 0, 0, 0);
            else ((ServerWorld) world).spawnParticles(effect, start.x, start.y, start.z, 1, 0, 0, 0, 0);
        }
    }

    public static Vec3d getRandomPos(Random random, double deltaX, double deltaY, double deltaZ) {
        return new Vec3d(getRandomCoordinate(random, deltaX), getRandomCoordinate(random, deltaY), getRandomCoordinate(random, deltaZ));
    }

    public static double getRandomCoordinate(Random random, double delta) {
        return (2 * random.nextDouble() - 1) * delta;
    }

    @FunctionalInterface
    public interface DummyConstructor<D extends ParticleEffect> {

        FeyParticleEffect<D> createDummy(ParticleType<D> particleType);
    }
}

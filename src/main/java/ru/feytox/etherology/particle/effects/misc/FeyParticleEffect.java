package ru.feytox.etherology.particle.effects.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public abstract class FeyParticleEffect<T extends ParticleEffect> implements ParticleEffect {
    protected final ParticleType<T> type;

    public FeyParticleEffect(ParticleType<T> type) {
        this.type = type;
    }

    public abstract T read(ParticleType<T> type, StringReader reader) throws CommandSyntaxException;
    public abstract T read(ParticleType<T> type, PacketByteBuf buf);
    public abstract String write();
    public abstract Codec<T> createCodec();

    @Override
    public String asString() {
        Identifier id = Registries.PARTICLE_TYPE.getId(this.getType());
        return id + " " + write();
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public Factory<T> createFactory() {
        return new Factory<>() {
            @Override
            public T read(ParticleType<T> type, StringReader reader) throws CommandSyntaxException {
                return FeyParticleEffect.this.read(type, reader);
            }

            @Override
            public T read(ParticleType<T> type, PacketByteBuf buf) {
                return FeyParticleEffect.this.read(type, buf);
            }
        };
    }

    public void spawnParticles(World world, int count, double delta, Vec3d centerPos) {
        spawnParticles(world, count, delta, delta, delta, centerPos);
    }

    public void spawnParticles(World world, int count, double deltaX, double deltaY, double deltaZ, Vec3d centerPos) {
        Random random = world.getRandom();
        for (int i = 0; i < count; i++) {
            Vec3d start = centerPos.add(getRandomPos(random, deltaX, deltaY, deltaZ));

            if (world.isClient) world.addParticle(this, start.x, start.y, start.z, 0, 0, 0);
            else ((ServerWorld) world).spawnParticles(this, start.x, start.y, start.z, 1, 0, 0, 0, 0);
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

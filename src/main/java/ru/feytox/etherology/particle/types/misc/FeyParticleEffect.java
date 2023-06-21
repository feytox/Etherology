package ru.feytox.etherology.particle.types.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

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

    public void spawnParticles(ClientWorld world, int count, double delta, Vec3d centerPos) {
        Random random = world.getRandom();
        for (int i = 0; i < count; i++) {
            Vec3d start = centerPos.add(getRandomCoordinate(random, delta), getRandomCoordinate(random, delta), getRandomCoordinate(random, delta));
            world.addParticle(this, start.x, start.y, start.z, 0, 0, 0);
        }
    }

    private double getRandomCoordinate(Random random, double delta) {
        return (2 * random.nextDouble() - 1) * delta;
    }

    @FunctionalInterface
    public interface DummyConstructor<D extends ParticleEffect> {

        FeyParticleEffect<D> createDummy(ParticleType<D> particleType);
    }
}

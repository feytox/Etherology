package ru.feytox.etherology.particle.types.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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

    @FunctionalInterface
    public interface DummyConstructor<D extends ParticleEffect> {

        FeyParticleEffect<D> createDummy(ParticleType<D> particleType);
    }
}

package ru.feytox.etherology.network.interaction;

import lombok.val;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.deprecated.EVec3d;
import ru.feytox.etherology.util.misc.CodecUtil;
import ru.feytox.etherology.util.misc.EIdentifier;

public record RedstoneLensStreamS2C(Vec3d start, Vec3d end, boolean isMiss) implements AbstractS2CPacket {

    public static final Id<RedstoneLensStreamS2C> ID = new Id<>(EIdentifier.of("redstone_lens_stream_s2c"));
    public static final PacketCodec<RegistryByteBuf, RedstoneLensStreamS2C> CODEC = PacketCodec.tuple(CodecUtil.VEC3D_PACKET, RedstoneLensStreamS2C::start, CodecUtil.VEC3D_PACKET, RedstoneLensStreamS2C::end, PacketCodecs.BOOL, RedstoneLensStreamS2C::isMiss, RedstoneLensStreamS2C::new);

    public static void receive(RedstoneLensStreamS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            ClientWorld world = client.world;
            if (world == null) return;

            val effect = new SimpleParticleEffect(EtherParticleTypes.REDSTONE_FLASH);
            EVec3d.lineOf(packet.start, packet.end, 0.5d)
                    .forEach(pos -> effect.spawnParticles(world, 1, 0, pos));
            if (packet.isMiss) return;

            val redstoneEffect = new SimpleParticleEffect(EtherParticleTypes.REDSTONE_STREAM);
            redstoneEffect.spawnParticles(world, world.random.nextBetween(8, 12), 0.1, packet.end);
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

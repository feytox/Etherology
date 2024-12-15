package ru.feytox.etherology.network.interaction;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.util.misc.CodecUtil;
import ru.feytox.etherology.util.misc.EIdentifier;

public record RedstoneLensStreamS2C(Vec3d start, Vec3d end, boolean isMiss) implements AbstractS2CPacket {

    public static final Id<RedstoneLensStreamS2C> ID = new Id<>(EIdentifier.of("redstone_lens_stream_s2c"));
    public static final PacketCodec<RegistryByteBuf, RedstoneLensStreamS2C> CODEC = PacketCodec.tuple(CodecUtil.VEC3D_PACKET, RedstoneLensStreamS2C::start, CodecUtil.VEC3D_PACKET, RedstoneLensStreamS2C::end, PacketCodecs.BOOL, RedstoneLensStreamS2C::isMiss, RedstoneLensStreamS2C::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

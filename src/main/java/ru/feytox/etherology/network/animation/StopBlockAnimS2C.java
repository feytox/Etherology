package ru.feytox.etherology.network.animation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.util.gecko.EGeo2BlockEntity;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;

public record StopBlockAnimS2C(BlockPos pos, String animName) implements AbstractS2CPacket {

    public static final Id<StopBlockAnimS2C> ID = new Id<>(EIdentifier.of("stop_block_anim"));
    public static final PacketCodec<RegistryByteBuf, StopBlockAnimS2C> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, StopBlockAnimS2C::pos, PacketCodecs.STRING, StopBlockAnimS2C::animName, StopBlockAnimS2C::new);

    @Deprecated
    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTrackingOld(T blockEntity, String animName) {
        new StopBlockAnimS2C(blockEntity.getPos(), animName).sendForTracking(blockEntity);
    }

    public static <T extends BlockEntity & EGeo2BlockEntity> void sendForTracking(T blockEntity, String animName) {
        new StopBlockAnimS2C(blockEntity.getPos(), animName).sendForTracking(blockEntity);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

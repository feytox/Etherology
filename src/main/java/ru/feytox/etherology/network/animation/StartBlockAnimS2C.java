package ru.feytox.etherology.network.animation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;

public record StartBlockAnimS2C(BlockPos pos, String animName) implements AbstractS2CPacket {

    public static final Id<StartBlockAnimS2C> ID = new Id<>(EIdentifier.of("start_block_anim"));
    public static final PacketCodec<RegistryByteBuf, StartBlockAnimS2C> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, StartBlockAnimS2C::pos, PacketCodecs.STRING, StartBlockAnimS2C::animName, StartBlockAnimS2C::new);

    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTracking(T blockEntity, String animName) {
        new StartBlockAnimS2C(blockEntity.getPos(), animName).sendForTracking(blockEntity);
    }

    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTracking(T blockEntity, String animName, PlayerEntity except) {
        new StartBlockAnimS2C(blockEntity.getPos(), animName).sendForTracking(blockEntity, except.getId());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

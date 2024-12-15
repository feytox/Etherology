package ru.feytox.etherology.network.interaction;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.util.misc.EIdentifier;

public record RemoveBlockEntityS2C(BlockPos pos) implements AbstractS2CPacket {

    public static final Id<RemoveBlockEntityS2C> ID = new Id<>(EIdentifier.of("remove_block_entity_s2c"));
    public static final PacketCodec<RegistryByteBuf, RemoveBlockEntityS2C> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, RemoveBlockEntityS2C::pos, RemoveBlockEntityS2C::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

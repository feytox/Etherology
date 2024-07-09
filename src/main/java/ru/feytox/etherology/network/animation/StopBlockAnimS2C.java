package ru.feytox.etherology.network.animation;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;

@Deprecated
public record StopBlockAnimS2C(BlockPos pos, String animName) implements AbstractS2CPacket {

    public static final Id<StopBlockAnimS2C> ID = new Id<>(EIdentifier.of("stop_block_anim"));
    public static final PacketCodec<RegistryByteBuf, StopBlockAnimS2C> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, StopBlockAnimS2C::pos, PacketCodecs.STRING, StopBlockAnimS2C::animName, StopBlockAnimS2C::new);

    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTracking(T blockEntity, String animName) {
        new StopBlockAnimS2C(blockEntity.getPos(), animName).sendForTracking(blockEntity);
    }

    public static void receive(StopBlockAnimS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(packet.pos);

            if (be instanceof EGeoBlockEntity geoBlock) {
                geoBlock.stopClientAnim(packet.animName);
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

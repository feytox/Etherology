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
import ru.feytox.etherology.util.gecko.EGeo2BlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;

public record Stop2BlockAnimS2C(BlockPos pos, String animName) implements AbstractS2CPacket {

    public static final Id<Stop2BlockAnimS2C> ID = new Id<>(EIdentifier.of("stop_2block_anim"));
    public static final PacketCodec<RegistryByteBuf, Stop2BlockAnimS2C> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, Stop2BlockAnimS2C::pos, PacketCodecs.STRING, Stop2BlockAnimS2C::animName, Stop2BlockAnimS2C::new);

    public static <T extends BlockEntity & EGeo2BlockEntity> void sendForTracking(T blockEntity, String animName) {
        new Stop2BlockAnimS2C(blockEntity.getPos(), animName).sendForTracking(blockEntity);
    }

    public static void receive(Stop2BlockAnimS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(packet.pos);

            if (be instanceof EGeo2BlockEntity animatable) {
                animatable.stopClientAnim(packet.animName);
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

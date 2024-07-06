package ru.feytox.etherology.network.animation;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.util.gecko.EGeo2BlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor
public class Stop2BlockAnimS2C extends AbstractS2CPacket {
    public static final Identifier STOP_2BLOCK_ANIM_PACKET_ID = EIdentifier.of("stop_2block_anim_packet");
    private final BlockPos blockPos;
    private final String animName;

    public static <T extends BlockEntity & EGeo2BlockEntity> void sendForTracking(T blockEntity, String animName) {
        Stop2BlockAnimS2C packet = new Stop2BlockAnimS2C(blockEntity.getPos(), animName);
        packet.sendForTracking(blockEntity);
    }

    public static void receive(S2CPacketInfo packetInfo) {
        MinecraftClient client = packetInfo.client();
        PacketByteBuf buf = packetInfo.buf();
        BlockPos blockPos = buf.readBlockPos();
        String animName = buf.readString();

        client.execute(() -> {
            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(blockPos);

            if (be instanceof EGeo2BlockEntity animatable) {
                animatable.stopClientAnim(animName);
            }
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeString(animName);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return STOP_2BLOCK_ANIM_PACKET_ID;
    }
}

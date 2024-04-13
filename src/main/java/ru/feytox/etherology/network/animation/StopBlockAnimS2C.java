package ru.feytox.etherology.network.animation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;

@Deprecated
public class StopBlockAnimS2C extends AbstractS2CPacket {
    public static final Identifier STOP_BLOCK_ANIM_PACKET_ID = new EIdentifier("stop_block_anim_packet");
    private final BlockPos blockPos;
    private final String animName;

    public <T extends BlockEntity & EGeoBlockEntity> StopBlockAnimS2C(T blockEntity, String animName) {
        this.blockPos = blockEntity.getPos();
        this.animName = animName;
    }

    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTracking(T blockEntity, String animName) {
        StopBlockAnimS2C packet = new StopBlockAnimS2C(blockEntity, animName);
        EtherologyNetwork.sendForTracking(packet, blockEntity);
    }

    public static void receive(S2CPacketInfo packetInfo) {
        MinecraftClient client = packetInfo.client();
        PacketByteBuf buf = packetInfo.buf();
        BlockPos blockPos = buf.readBlockPos();
        String animName = buf.readString();

        client.execute(() -> {
            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(blockPos);

            if (be instanceof EGeoBlockEntity geoBlock) {
                geoBlock.stopClientAnim(animName);
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
        return STOP_BLOCK_ANIM_PACKET_ID;
    }
}

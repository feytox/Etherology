package ru.feytox.etherology.network.animation;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;

@RequiredArgsConstructor
public class SwitchBlockAnimS2C extends AbstractS2CPacket {
    public static final Identifier SWITCH_BLOCK_ANIM_PACKET_ID = new EIdentifier("switch_block_anim");
    private final BlockPos blockPos;
    private final String stopAnim;
    private final String startAnim;

    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTracking(T blockEntity, String stopAnim, String startAnim) {
        SwitchBlockAnimS2C packet = new SwitchBlockAnimS2C(blockEntity.getPos(), stopAnim, startAnim);
        EtherologyNetwork.sendForTracking(packet, blockEntity);
    }

    public static void receive(S2CPacketInfo packetInfo) {
        MinecraftClient client = packetInfo.client();
        PacketByteBuf buf = packetInfo.buf();
        BlockPos blockPos = buf.readBlockPos();
        String stopAnim = buf.readString();
        String startAnim = buf.readString();

        client.execute(() -> {
            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(blockPos);

            if (be instanceof EGeoBlockEntity eGeoBlock) {
                eGeoBlock.stopAnim(stopAnim);
                eGeoBlock.triggerAnim(startAnim);
            }
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeString(stopAnim);
        buf.writeString(startAnim);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return SWITCH_BLOCK_ANIM_PACKET_ID;
    }
}

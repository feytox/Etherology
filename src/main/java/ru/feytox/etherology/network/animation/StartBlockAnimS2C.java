package ru.feytox.etherology.network.animation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;

public class StartBlockAnimS2C extends AbstractS2CPacket {
    public static final Identifier START_BLOCK_ANIM_PACKET_ID = new EIdentifier("start_block_anim");
    private final BlockPos blockPos;
    private final String animName;

    public <T extends BlockEntity & EGeoBlockEntity> StartBlockAnimS2C(T blockEntity, String animName) {
        this.blockPos = blockEntity.getPos();
        this.animName = animName;
    }

    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTracking(T blockEntity, String animName) {
        StartBlockAnimS2C packet = new StartBlockAnimS2C(blockEntity, animName);
        EtherologyNetwork.sendForTracking(packet, blockEntity);
    }

    public static <T extends BlockEntity & EGeoBlockEntity> void sendForTracking(T blockEntity, String animName, PlayerEntity except) {
        StartBlockAnimS2C packet = new StartBlockAnimS2C(blockEntity, animName);
        EtherologyNetwork.sendForTracking(packet, blockEntity, except.getId());
    }

    public static void receive(S2CPacketInfo packetInfo) {
        MinecraftClient client = packetInfo.client();
        PacketByteBuf buf = packetInfo.buf();
        BlockPos blockPos = buf.readBlockPos();
        String animName = buf.readString();

        client.execute(() -> {
            if (client.world == null) return;
            BlockEntity be = client.world.getBlockEntity(blockPos);

            if (be instanceof EGeoBlockEntity eGeoBlock) {
                eGeoBlock.triggerAnim(animName);
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
        return START_BLOCK_ANIM_PACKET_ID;
    }
}

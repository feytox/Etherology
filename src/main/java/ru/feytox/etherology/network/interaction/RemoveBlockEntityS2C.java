package ru.feytox.etherology.network.interaction;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor
public class RemoveBlockEntityS2C extends AbstractS2CPacket {

    public static final Identifier ID = new EIdentifier("remove_block_entity_s2c");

    private final BlockPos blockPos;

    public static void receive(S2CPacketInfo packetInfo) {
        MinecraftClient client = packetInfo.client();
        BlockPos pos = packetInfo.buf().readBlockPos();

        client.execute(() -> {
            if (client.world == null) return;
            client.world.removeBlockEntity(pos);
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeBlockPos(blockPos);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return ID;
    }
}

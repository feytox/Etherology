package ru.feytox.etherology.network.interaction;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.particle.PealWaveParticle;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@RequiredArgsConstructor
public class HammerPealS2C extends AbstractS2CPacket {
    public static final Identifier HAMMER_PEAL_S2C_ID = new EIdentifier("hammer_peal_s2c");
    private final int fromEntityId;
    private final int toEntityId;

    public static void receive(S2CPacketInfo packetInfo) {
        PacketByteBuf buf = packetInfo.buf();
        MinecraftClient client = packetInfo.client();
        ClientWorld world = client.world;
        if (world == null) return;

        int fromEntityId = buf.readInt();
        int toEntityId = buf.readInt();

        client.execute(() -> {
            Entity fromEntity = world.getEntityById(fromEntityId);
            Entity toEntity = world.getEntityById(toEntityId);
            if (fromEntity == null || toEntity == null) return;

            PealWaveParticle.spawnWave(world, fromEntity, toEntity);
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeInt(fromEntityId);
        buf.writeInt(toEntityId);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return HAMMER_PEAL_S2C_ID;
    }
}

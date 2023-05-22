package ru.feytox.etherology.network.animation;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.C2SPacketInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class HammerSwingC2S extends AbstractC2SPacket {

    public static final Identifier HAMMER_SWING_C2S_ID = new EIdentifier("hammer_swing_c2s");
    private final float attackCooldown;

    public HammerSwingC2S(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeFloat(attackCooldown);
        return buf;
    }

    public static void receive(C2SPacketInfo packetInfo) {
        float attackCooldown = packetInfo.buf().readFloat();

        MinecraftServer server = packetInfo.server();
        ServerPlayerEntity sender = packetInfo.player();
        ServerWorld world = sender.getWorld();

        server.execute(() -> {
            if (!HammerItem.checkHammer(sender)) return;

            HammerSwingS2C packet = new HammerSwingS2C(sender.getId(), attackCooldown);
            EtherologyNetwork.sendForTracking(world, sender.getBlockPos(), sender.getId(), packet);
        });
    }

    @Override
    public Identifier getPacketID() {
        return HAMMER_SWING_C2S_ID;
    }
}

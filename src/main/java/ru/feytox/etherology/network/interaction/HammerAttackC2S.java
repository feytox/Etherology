package ru.feytox.etherology.network.interaction;

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
import ru.feytox.etherology.util.feyapi.ShockwaveUtil;

public class HammerAttackC2S extends AbstractC2SPacket {

    public static final Identifier HAMMER_ATTACK_C2S_ID = new EIdentifier("hammer_attack_c2s");
    private final boolean attackGround;

    public HammerAttackC2S(boolean attackGround) {
        this.attackGround = attackGround;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeBoolean(attackGround);
        return buf;
    }

    public static void receive(C2SPacketInfo packetInfo) {
        boolean attackGround = packetInfo.buf().readBoolean();
        MinecraftServer server = packetInfo.server();
        ServerPlayerEntity sender = packetInfo.player();
        ServerWorld world = sender.getWorld();
        float attackCooldown = sender.getAttackCooldownProgress(0.0f);

        server.execute(() -> {
            if (!HammerItem.checkHammer(sender)) return;

            if (attackGround && attackCooldown == 1) ShockwaveUtil.onFullAttack(sender);

            HammerAttackS2C packet = new HammerAttackS2C(sender.getId(), attackCooldown, attackGround);
            EtherologyNetwork.sendForTracking(world, sender.getBlockPos(), sender.getId(), packet);
        });
    }

    @Override
    public Identifier getPacketID() {
        return HAMMER_ATTACK_C2S_ID;
    }
}

package ru.feytox.etherology.network.interaction;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.TwoHandheldSword;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.C2SPacketInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.ShockwaveUtil;

public class TwoHandHeldAttackC2S extends AbstractC2SPacket {

    public static final Identifier TWOHANDHELD_ATTACK_C2S_ID = new EIdentifier("twohandheld_attack_c2s");
    private final boolean attackGround;
    private final boolean isHammer;

    public TwoHandHeldAttackC2S(boolean attackGround, boolean isHammer) {
        this.attackGround = attackGround;
        this.isHammer = isHammer;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeBoolean(attackGround);
        buf.writeBoolean(isHammer);
        return buf;
    }

    public static void receive(C2SPacketInfo packetInfo) {
        boolean attackGround = packetInfo.buf().readBoolean();
        boolean isHammer = packetInfo.buf().readBoolean();
        MinecraftServer server = packetInfo.server();
        ServerPlayerEntity sender = packetInfo.player();
        ServerWorld world = sender.getWorld();
        float attackCooldown = sender.getAttackCooldownProgress(0.0f);

        server.execute(() -> {
            if (!TwoHandheldSword.check(sender, TwoHandheldSword.class)) return;

            if (attackGround && attackCooldown > 0.9) ShockwaveUtil.onFullAttack(sender);

            TwoHandHeldAttackS2C packet = new TwoHandHeldAttackS2C(sender.getId(), attackCooldown, attackGround, isHammer);
            EtherologyNetwork.sendForTracking(world, sender.getBlockPos(), sender.getId(), packet);
        });
    }

    @Override
    public Identifier getPacketID() {
        return TWOHANDHELD_ATTACK_C2S_ID;
    }
}

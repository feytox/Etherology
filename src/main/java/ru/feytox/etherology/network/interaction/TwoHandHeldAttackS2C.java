package ru.feytox.etherology.network.interaction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.animation.TriggerAnimations;
import ru.feytox.etherology.animation.TriggerPlayerAnimation;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.item.TwoHandheldSword;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.particle.ShockwaveParticle;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;
import ru.feytox.etherology.util.feyapi.ShockwaveUtil;

public class TwoHandHeldAttackS2C extends AbstractS2CPacket {

    public static final Identifier TWOHANDHELD_ATTACK_S2C_ID = new EIdentifier("twohandheld_attack_s2c");
    private final int senderId;
    private final float attackCooldown;
    private final boolean attackGround;
    private final boolean isHammer;

    public TwoHandHeldAttackS2C(int senderId, float attackCooldown, boolean attackGround, boolean isHammer) {
        this.senderId = senderId;
        this.attackCooldown = attackCooldown;
        this.attackGround = attackGround;
        this.isHammer = isHammer;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeInt(senderId);
        buf.writeFloat(attackCooldown);
        buf.writeBoolean(attackGround);
        buf.writeBoolean(isHammer);
        return buf;
    }

    public static void receive(S2CPacketInfo packetInfo) {
        int senderId = packetInfo.buf().readInt();
        float attackCooldown = packetInfo.buf().readFloat();
        boolean attackGround = packetInfo.buf().readBoolean();
        boolean isHammer = packetInfo.buf().readBoolean();

        MinecraftClient client = packetInfo.client();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ClientWorld world = (ClientWorld) player.world;

        client.execute(() -> {
            Entity entity = world.getEntityById(senderId);
            if (!(entity instanceof AbstractClientPlayerEntity swinger)) return;
            if (!(swinger instanceof EtherologyPlayer animatedPlayer)) return;
            if (!TwoHandheldSword.check(swinger, TwoHandheldSword.class)) return;

            boolean isStrongAttack = attackCooldown > 0.9F;
            TriggerPlayerAnimation animation = TriggerAnimations.getTwohandheldAnim(swinger.getMainArm(), isHammer, isStrongAttack);
            animation.play(animatedPlayer, 0, null);

            if (!isStrongAttack || !isHammer) return;
            if (!HammerItem.checkHammer(swinger)) return;
            if (attackGround) ShockwaveUtil.onFullAttack(swinger);

            swinger.playSound(EtherSounds.HAMMER_SWING, SoundCategory.PLAYERS, 0.5f, 0.9f);
            ShockwaveParticle.spawnShockParticles(world, swinger);
        });
    }

    @Override
    public Identifier getPacketID() {
        return TWOHANDHELD_ATTACK_S2C_ID;
    }
}

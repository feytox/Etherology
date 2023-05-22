package ru.feytox.etherology.network.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;
import ru.feytox.etherology.util.feyapi.PlayerAnimations;

public class HammerSwingS2C extends AbstractS2CPacket {

    public static final Identifier HAMMER_SWING_S2C_ID = new EIdentifier("hammer_swing_s2c");
    private final int senderId;
    private final float attackCooldown;

    public HammerSwingS2C(int senderId, float attackCooldown) {
        this.senderId = senderId;
        this.attackCooldown = attackCooldown;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeInt(senderId);
        buf.writeFloat(attackCooldown);
        return buf;
    }

    public static void receive(S2CPacketInfo packetInfo) {
        int senderId = packetInfo.buf().readInt();
        float attackCooldown = packetInfo.buf().readFloat();

        MinecraftClient client = packetInfo.client();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ClientWorld world = (ClientWorld) player.world;

        client.execute(() -> {
            Entity entity = world.getEntityById(senderId);
            if (!(entity instanceof AbstractClientPlayerEntity swinger)) return;
            if (!(swinger instanceof IAnimatedPlayer animatedPlayer)) return;
            if (!HammerItem.checkHammer(swinger)) return;

            String animString = swinger.getMainArm().equals(Arm.LEFT) ? "right.twohanded.hit": "left.twohanded.hit";
            PlayerAnimations idle = swinger.getMainArm().equals(Arm.LEFT) ? PlayerAnimations.LEFT_TWOHANDED_IDLE : PlayerAnimations.RIGHT_TWOHANDED_IDLE;
            PlayerAnimations.setAnimation(animatedPlayer, false,
                    new PlayerAnimations.AnimationData(new EIdentifier(animString), 0, Ease.OUTQUART, true),
                    null, idle);

            if (attackCooldown == 1.0F) {
                float pitchVal = 0.9f + world.random.nextFloat() * 0.2f;
                swinger.playSound(EtherSounds.HAMMER_SWING, SoundCategory.PLAYERS, 0.5f, pitchVal);
            }
        });
    }

    @Override
    public Identifier getPacketID() {
        return HAMMER_SWING_S2C_ID;
    }
}

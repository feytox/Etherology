package ru.feytox.etherology.network.animation;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.UUID;

public class PlayerAnimationS2C extends AbstractS2CPacket {
    private static final EIdentifier PLAY_ANIMATION_PACKET_ID = new EIdentifier("play_animation_packet");
    private final PlayerEntity relatedPlayer;
    private final Identifier animationID;

    public PlayerAnimationS2C(PlayerEntity relatedPlayer, Identifier animationID) {
        this.relatedPlayer = relatedPlayer;
        this.animationID = animationID;
    }

    public static PlayerAnimationS2C dummy() {
        return new PlayerAnimationS2C(null, new Identifier("null:null"));
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) return;
        Entity entity = client.world.getEntityById(buf.readInt());
        if (!(entity instanceof IAnimatedPlayer animatedPlayer)) return;
        Identifier animationID = buf.readIdentifier();

        client.execute(() -> {
            var animationContainer = animatedPlayer.getEtherologyAnimation();
            KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(animationID);
            UUID currentAnimUUID = null;
            KeyframeAnimationPlayer currentAnim = null;
            if (animationContainer.getAnimation() instanceof KeyframeAnimationPlayer playAnim) {
                currentAnim = playAnim;
                currentAnimUUID = currentAnim.getData().getUuid();
            }

            if (anim == null) return;
            boolean animEquality = anim.getUuid().equals(currentAnimUUID);
            if (animEquality && currentAnim.isActive()) return;
            animationContainer.setAnimation(new KeyframeAnimationPlayer(anim));
        });
    }

    @Override
    public PacketByteBuf encode() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(relatedPlayer.getId());
        buf.writeIdentifier(animationID);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return PLAY_ANIMATION_PACKET_ID;
    }
}

package ru.feytox.etherology.network.animation;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.UUID;

public class PlayerAnimationS2C extends AbstractS2CPacket {
    public static final EIdentifier PLAYER_ANIMATION_PACKET_ID = new EIdentifier("player_animation_packet");
    private final PlayerEntity relatedPlayer;
    private final Identifier animationID;
    private boolean isStop;
    private int fadeLength = -1;
    private Ease fadeEase = null;
    private FirstPersonConfiguration firstPersonConfiguration = new FirstPersonConfiguration();
    private FirstPersonMode firstPersonMode = FirstPersonMode.DISABLED;

    public PlayerAnimationS2C(PlayerEntity relatedPlayer, Identifier animationID, boolean isStop) {
        this.relatedPlayer = relatedPlayer;
        this.animationID = animationID;
        this.isStop = isStop;
    }

    public static void receive(S2CPacketInfo packetInfo) {
        MinecraftClient client = packetInfo.client();
        PacketByteBuf buf = packetInfo.buf();

        if (client.world == null) return;
        Entity entity = client.world.getEntityById(buf.readInt());
        if (!(entity instanceof IAnimatedPlayer animatedPlayer)) return;
        Identifier animationID = buf.readIdentifier();
        boolean isStop = buf.readBoolean();

        int fadeLength = buf.readInt();
        Ease fadeEase = fadeLength == -1 ? null : Ease.getEase(buf.readByte());

        FirstPersonMode firstPersonMode = FirstPersonMode.DISABLED;
        try {
            firstPersonMode = FirstPersonMode.valueOf(buf.readString());
        } catch (Exception ignored) {}
        FirstPersonConfiguration firstPersonConfiguration = new FirstPersonConfiguration(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());

        FirstPersonMode finalFirstPersonMode = firstPersonMode;
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
            if (animEquality && currentAnim.isActive()) {
                if (isStop) setAnimation(animationContainer, null, fadeLength, fadeEase);
                return;
            }
            if (!animEquality && isStop) return;
            KeyframeAnimationPlayer animation = new KeyframeAnimationPlayer(anim);
            animation.setFirstPersonMode(finalFirstPersonMode).setFirstPersonConfiguration(firstPersonConfiguration);
            setAnimation(animationContainer, animation, fadeLength, fadeEase);
        });
    }

    private static void setAnimation(ModifierLayer<IAnimation> animationContainer, IAnimation animation, int fadeLength, Ease fadeEase) {
        if (fadeEase == null) animationContainer.setAnimation(animation);
        else {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeLength, fadeEase), animation, true);
        }
    }

    public PlayerAnimationS2C setFirstPersonConfiguration(FirstPersonConfiguration firstPersonConfiguration) {
        this.firstPersonConfiguration = firstPersonConfiguration;
        return this;
    }

    public PlayerAnimationS2C setFirstPersonMode(FirstPersonMode firstPersonMode) {
        this.firstPersonMode = firstPersonMode;
        return this;
    }

    public PlayerAnimationS2C setStop(boolean isStop) {
        this.isStop = isStop;
        return this;
    }

    public PlayerAnimationS2C setFade(int length, Ease ease) {
        fadeLength = length;
        fadeEase = ease;
        return this;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeInt(relatedPlayer.getId());
        buf.writeIdentifier(animationID);
        buf.writeBoolean(isStop);
        buf.writeInt(fadeLength);
        buf.writeByte(fadeEase.getId());
        buf.writeString(firstPersonMode.name());
        buf.writeBoolean(firstPersonConfiguration.isShowLeftArm());
        buf.writeBoolean(firstPersonConfiguration.isShowLeftItem());
        buf.writeBoolean(firstPersonConfiguration.isShowRightArm());
        buf.writeBoolean(firstPersonConfiguration.isShowRightItem());
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return PLAYER_ANIMATION_PACKET_ID;
    }
}

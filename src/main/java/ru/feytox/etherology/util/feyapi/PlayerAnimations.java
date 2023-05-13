package ru.feytox.etherology.util.feyapi;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.registry.item.EItems;

import java.util.UUID;
import java.util.function.Predicate;

public enum PlayerAnimations {
    CRATE_CARRYING(new AnimationData(new EIdentifier("animation.player.carry"), 5, Ease.INOUTCUBIC, true),
            player -> player.getMainHandStack().isOf(EItems.CARRIED_CRATE));

    private final Predicate<AbstractClientPlayerEntity> playPredicate;
    private final AnimationData animationData;

    PlayerAnimations(AnimationData animationData, Predicate<AbstractClientPlayerEntity> playPredicate) {
        this.playPredicate = playPredicate;
        this.animationData = animationData;
    }

    public static void tickAnimations(AbstractClientPlayerEntity player) {
        if (!(player instanceof IAnimatedPlayer animatedPlayer)) return;
        boolean wasTicked = animatedPlayer.hadEtherologyAnimationsTick();
        for (int i = 0; i < values().length; i++) {
            PlayerAnimations animation = values()[i];
            boolean lastState = animatedPlayer.getLastAnimState(animation);
            boolean state = animation.test(player);
            if (lastState == state) continue;
            animatedPlayer.setAnimState(animation, state);

            if (state) animation.setAnimation(animatedPlayer, !wasTicked);
            else animatedPlayer.stopAnim(animation);
        }
        if (!wasTicked) animatedPlayer.setHadEtherologyAnimationsTick();
    }

    public boolean test(AbstractClientPlayerEntity player) {
        return playPredicate.test(player);
    }

    public void setAnimation(IAnimatedPlayer player, boolean shouldFade) {
        var animationContainer = player.getEtherologyAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(animationData.animationID);
        if (anim == null) return;

        UUID currentAnimUUID = null;
        KeyframeAnimationPlayer currentAnim = null;
        if (animationContainer.getAnimation() instanceof KeyframeAnimationPlayer playAnim) {
            currentAnim = playAnim;
            currentAnimUUID = currentAnim.getData().getUuid();
        }
        boolean animEquality = anim.getUuid().equals(currentAnimUUID);
        if (animEquality && currentAnim.isActive()) return;
        KeyframeAnimationPlayer animation = new KeyframeAnimationPlayer(anim);
        if (animationData.firstPerson) {
            animation.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
            animation.setFirstPersonConfiguration(new FirstPersonConfiguration(true, true, true, true));
        }
        if (shouldFade) animationContainer.setAnimation(animation);
        else animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(animationData.length, animationData.ease), animation, true);
    }

    public record AnimationData(Identifier animationID, int length, Ease ease, boolean firstPerson) {}
}

package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import lombok.experimental.UtilityClass;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.registry.custom.EtherologyRegistry;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class PlayerAnimationController {

    /**
     * Updates the animations for the given player.
     *
     * @param  player  the player whose animations need to be updated
     */
    public static void tickAnimations(AbstractClientPlayerEntity player) {
        if (!(player instanceof EtherologyPlayer animatedPlayer)) return;

        List<PredicatePlayerAnimation> predicateAnimations = EtherologyRegistry.getAll(PredicatePlayerAnimation.class);
        for (PredicatePlayerAnimation animation : predicateAnimations) {
            boolean lastState = animatedPlayer.etherology$getLastAnimState(animation);
            boolean predicateState = animation.test(player);
            if (predicateState == lastState) continue;

            if (predicateState) animation.play(animatedPlayer);
            else animatedPlayer.etherology$stopAnim(animation);
        }
    }

    /**
     * Plays an Etherology animation for the Etherology player.
     *
     * @param  player          the Etherology player
     * @param  playerAnimation the Etherology animation to play
     * @param  easeLength      the length of the ease
     * @param  ease            the ease type
     * @return                 true if the animation was started successfully or already playing; false otherwise
     */
    public static boolean playAnimation(EtherologyPlayer player, AbstractPlayerAnimation playerAnimation, int easeLength, Ease ease) {
        var animationContainer = player.etherology$getAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(playerAnimation.getAnimationId());
        if (anim == null) return false;

        KeyframeAnimationPlayer currentAnim = null;
        if (animationContainer.getAnimation() instanceof KeyframeAnimationPlayer playAnim) {
            currentAnim = playAnim;
        }

        if (currentAnim instanceof EtherKeyframe currentKAP && !currentAnim.isActive()) {
            List<Identifier> replacements = playerAnimation.getReplacements();
            if (!replacements.isEmpty() && replacements.contains(currentKAP.getPlayerAnimation().getAnimationId())) {
                return false;
            }

            if (currentKAP.getPlayerAnimation().equals(playerAnimation) && !playerAnimation.isShouldBreak()) {
                return true;
            }
        }

        EtherKeyframe animation = new EtherKeyframe(anim, playerAnimation);
        animation.setupEndAction(player);

        if (playerAnimation.isFirstPerson()) {
            animation.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
            animation.setFirstPersonConfiguration(new FirstPersonConfiguration(true, true, true, true));
        }

        if (ease == null || easeLength == 0) animationContainer.setAnimation(animation);
        else animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(easeLength, ease), animation, true);

        Consumer<EtherologyPlayer> startAction = playerAnimation.getStartAction();
        if (startAction != null) {
            startAction.accept(player);
        }

        return true;
    }

    /**
     * Returns a Consumer that sets the hammer state of an EtherologyPlayer.
     *
     * @param  state  the hammer state to be set
     * @return        a Consumer that sets the hammer state of an EtherologyPlayer
     */
    public static Consumer<EtherologyPlayer> setHammerState(HammerState state) {
        return player -> player.etherology$setHammerState(state);
    }
}
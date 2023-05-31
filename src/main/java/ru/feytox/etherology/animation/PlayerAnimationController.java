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
import ru.feytox.etherology.util.feyapi.ExtendedKAP;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class PlayerAnimationController {

    public static void tickAnimations(AbstractClientPlayerEntity player) {
        if (!(player instanceof IAnimatedPlayer animatedPlayer)) return;

        List<PredicatePlayerAnimation> predicateAnimations = EtherologyRegistry.getAll(PredicatePlayerAnimation.class);
        for (PredicatePlayerAnimation animation : predicateAnimations) {
            boolean lastState = animatedPlayer.getLastAnimState(animation);
            boolean predicateState = animation.test(player);
            if (predicateState == lastState) continue;

            if (predicateState) animation.play(animatedPlayer);
            else animatedPlayer.stopAnim(animation);
        }
    }

    public static boolean playAnimation(IAnimatedPlayer player, AbstractPlayerAnimation playerAnimation, int easeLength, Ease ease) {
        var animationContainer = player.getEtherologyAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(playerAnimation.getAnimationId());
        if (anim == null) return false;

        KeyframeAnimationPlayer currentAnim = null;
        if (animationContainer.getAnimation() instanceof KeyframeAnimationPlayer playAnim) {
            currentAnim = playAnim;
        }

        if (currentAnim instanceof ExtendedKAP currentKAP && currentAnim.isActive()) {
            List<Identifier> replacements = playerAnimation.getReplacements();
            if (!replacements.isEmpty() && replacements.contains(currentKAP.getAnim().getAnimationId())) {
                return false;
            }

            if (currentKAP.getAnim().equals(playerAnimation) && !playerAnimation.isShouldBreak()) {
                return true;
            }
        }

        ExtendedKAP animation = new ExtendedKAP(anim, playerAnimation);
        animation.setupEndAction(player);

        if (playerAnimation.isFirstPerson()) {
            animation.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
            animation.setFirstPersonConfiguration(new FirstPersonConfiguration(true, true, true, true));
        }

        if (ease == null || easeLength == 0) animationContainer.setAnimation(animation);
        else animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(easeLength, ease), animation, true);

        Consumer<IAnimatedPlayer> startAction = playerAnimation.getStartAction();
        if (startAction != null) {
            startAction.accept(player);
        }

        return true;
    }

    public static Consumer<IAnimatedPlayer> setHammerState(HammerState state) {
        return player -> player.setHammerState(state);
    }
}

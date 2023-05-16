package ru.feytox.etherology.util.feyapi;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.FourStates;
import ru.feytox.etherology.mixin.KeyframeAnimationAccessor;
import ru.feytox.etherology.registry.item.EItems;

import java.util.UUID;
import java.util.function.Predicate;

// TODO: 15/05/2023 TL;DR: clean code.
//  Убрать лишний код, который связан со старыми попытками пофиксить баг с двойным ударом
public enum PlayerAnimations {
    CRATE_CARRYING(new AnimationData(new EIdentifier("crate.carry"), 5, Ease.INOUTCUBIC, true),
            player -> player.getMainHandStack().isOf(EItems.CARRIED_CRATE)),
    LEFT_TWOHANDED_IDLE(new AnimationData(new EIdentifier("right.twohanded.idle"), 0, Ease.INOUTCUBIC, true),
            new EIdentifier("right.twohanded.hit"),
            player -> AnimationPredicates.twohandedIdle(player, Arm.LEFT)),
    RIGHT_TWOHANDED_IDLE(new AnimationData(new EIdentifier("left.twohanded.idle"), 0, Ease.INOUTCUBIC, true),
            new EIdentifier("left.twohanded.hit"),
            player -> AnimationPredicates.twohandedIdle(player, Arm.RIGHT));

    private final Predicate<AbstractClientPlayerEntity> playPredicate;
    private final AnimationData animationInfo;
    private final Identifier replaceAnim;

    PlayerAnimations(AnimationData animationInfo, Predicate<AbstractClientPlayerEntity> playPredicate) {
        this(animationInfo, null, playPredicate);
    }

    PlayerAnimations(AnimationData animationInfo, Identifier replaceAnim, Predicate<AbstractClientPlayerEntity> playPredicate) {
        this.playPredicate = playPredicate;
        this.animationInfo = animationInfo;
        this.replaceAnim = replaceAnim;
    }

    public static void tickAnimations(AbstractClientPlayerEntity player) {
        if (!(player instanceof IAnimatedPlayer animatedPlayer)) return;
        boolean wasTicked = animatedPlayer.hadEtherologyAnimationsTick();
        for (int i = 0; i < values().length; i++) {
            PlayerAnimations animation = values()[i];
            FourStates lastState = animatedPlayer.getLastAnimState(animation);
            boolean state = animation.test(player);
            if (lastState.getValue() == state && !lastState.getModifier()) continue;

            boolean shouldFade = animation.animationInfo.length > 0;
            if (state || lastState.equals(FourStates.MUST_TRUE)) animation.setAnimation(animatedPlayer, wasTicked && shouldFade);
            else animatedPlayer.stopAnim(animation);
        }
        if (!wasTicked) animatedPlayer.setHadEtherologyAnimationsTick();
    }

    public boolean test(AbstractClientPlayerEntity player) {
        return playPredicate.test(player);
    }

    public void setAnimation(IAnimatedPlayer player, boolean shouldFade) {
        FourStates result = setAnimation(player, shouldFade, animationInfo, replaceAnim, null);
        player.setAnimState(this, result);
    }

    public static FourStates setAnimation(IAnimatedPlayer player, boolean shouldFade, AnimationData animationInfo, Identifier replaceAnim, @Nullable PlayerAnimations replacing) {
        var animationContainer = player.getEtherologyAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(animationInfo.animationID);
        if (anim == null) return FourStates.FALSE;
        if (anim.stopTick != anim.endTick && !anim.isInfinite) {
            ((KeyframeAnimationAccessor) (Object) anim).setStopTick(anim.endTick);
        }

        UUID currentAnimUUID = null;
        KeyframeAnimationPlayer currentAnim = null;
        if (animationContainer.getAnimation() instanceof KeyframeAnimationPlayer playAnim) {
            currentAnim = playAnim;
            currentAnimUUID = currentAnim.getData().getUuid();
        }
        if (currentAnim instanceof ExtendedKAP currentKAP && currentAnim.isActive() && !currentAnim.isLoopStarted()) {
            if (currentKAP.getId().equals(replaceAnim)) {
                return FourStates.MUST_FALSE;
            }
        }

        boolean animEquality = anim.getUuid().equals(currentAnimUUID);
        if (animEquality && currentAnim.isActive() && !currentAnim.isLoopStarted()) return FourStates.FALSE;
        ExtendedKAP animation = new ExtendedKAP(anim, animationInfo.animationID);
        if (animationInfo.firstPerson) {
            animation.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
            animation.setFirstPersonConfiguration(new FirstPersonConfiguration(true, true, true, true));
        }
        if (shouldFade) animationContainer.setAnimation(animation);
        else animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(animationInfo.length, animationInfo.ease), animation, true);

        if (replacing != null) player.setAnimState(replacing, FourStates.MUST_FALSE);

        return FourStates.TRUE;
    }

    public record AnimationData(Identifier animationID, int length, Ease ease, boolean firstPerson) {}
}

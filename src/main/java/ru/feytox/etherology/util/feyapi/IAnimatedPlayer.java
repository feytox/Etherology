package ru.feytox.etherology.util.feyapi;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;

public interface IAnimatedPlayer {
    ModifierLayer<IAnimation> getEtherologyAnimation();
    boolean getLastAnimState(PlayerAnimations anim);
    boolean hadEtherologyAnimationsTick();
    void setHadEtherologyAnimationsTick();
    void setAnimState(PlayerAnimations anim, boolean state);
    default void stopAnim(PlayerAnimations anim) {
        var animationContainer = getEtherologyAnimation();
        animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTCUBIC), null, true);
        setAnimState(anim, false);
    }
}

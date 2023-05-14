package ru.feytox.etherology.util.feyapi;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import ru.feytox.etherology.enums.FourStates;

public interface IAnimatedPlayer {
    ModifierLayer<IAnimation> getEtherologyAnimation();
    FourStates getLastAnimState(PlayerAnimations anim);
    boolean hadEtherologyAnimationsTick();
    void setHadEtherologyAnimationsTick();
    void setAnimState(PlayerAnimations anim, FourStates state);
    default void stopAnim(PlayerAnimations anim) {
        var animationContainer = getEtherologyAnimation();
        animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTCUBIC), null, true);
        setAnimState(anim, FourStates.FALSE);
    }
}

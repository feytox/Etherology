package ru.feytox.etherology.util.feyapi;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import ru.feytox.etherology.animation.AbstractPlayerAnimation;
import ru.feytox.etherology.enums.HammerState;

public interface IAnimatedPlayer {
    ModifierLayer<IAnimation> getEtherologyAnimation();
    boolean getLastAnimState(AbstractPlayerAnimation anim);
    void setAnimState(AbstractPlayerAnimation anim, boolean state);
    default void stopAnim(AbstractPlayerAnimation anim) {
        var animationContainer = getEtherologyAnimation();
        animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTCUBIC), null, true);
        setAnimState(anim, false);
    }
    HammerState getHammerState();
    void setHammerState(HammerState state);
}

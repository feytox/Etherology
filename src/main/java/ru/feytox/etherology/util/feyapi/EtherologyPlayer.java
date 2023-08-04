package ru.feytox.etherology.util.feyapi;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import ru.feytox.etherology.animation.AbstractPlayerAnimation;
import ru.feytox.etherology.enums.HammerState;

public interface EtherologyPlayer {
    ModifierLayer<IAnimation> etherology$getAnimation();
    boolean etherology$getLastAnimState(AbstractPlayerAnimation anim);
    void etherology$setAnimState(AbstractPlayerAnimation anim, boolean state);
    default void etherology$stopAnim(AbstractPlayerAnimation anim) {
        var animationContainer = etherology$getAnimation();
        animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTCUBIC), null, true);
        etherology$setAnimState(anim, false);
    }

    HammerState etherology$getHammerState();
    void etherology$setHammerState(HammerState state);
}

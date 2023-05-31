package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.registry.custom.EtherologyRegistry;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.function.Consumer;

public class TriggerAnimations {
    public static final TriggerablePlayerAnimation LEFT_HAMMER_HIT = new TriggerablePlayerAnimation(new EIdentifier("left_hammer_hit"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            play("left_hammer_idle"));
    public static final TriggerablePlayerAnimation RIGHT_HAMMER_HIT = new TriggerablePlayerAnimation(new EIdentifier("right_hammer_hit"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            play("right_hammer_idle"));
    public static final TriggerablePlayerAnimation LEFT_HAMMER_HIT_WEAK = new TriggerablePlayerAnimation(new EIdentifier("left_hammer_hit_weak"), false, true,
            PlayerAnimationController.setHammerState(HammerState.WEAK_ATTACK),
            play("left_hammer_idle"));
    public static final TriggerablePlayerAnimation RIGHT_HAMMER_HIT_WEAK = new TriggerablePlayerAnimation(new EIdentifier("right_hammer_hit_weak"), false, true,
            PlayerAnimationController.setHammerState(HammerState.WEAK_ATTACK),
            play("right_hammer_idle"));

    private static Consumer<IAnimatedPlayer> play(String id) {
        return player -> {
            AbstractPlayerAnimation anim = EtherologyRegistry.getAndCast(PredicatePlayerAnimation.class, new EIdentifier(id));
            if (anim == null) return;
            anim.play(player, 2, Ease.INCUBIC);
        };
    }

    public static void registerAll() {
        LEFT_HAMMER_HIT.register();
        RIGHT_HAMMER_HIT.register();
        LEFT_HAMMER_HIT_WEAK.register();
        RIGHT_HAMMER_HIT_WEAK.register();
    }
}

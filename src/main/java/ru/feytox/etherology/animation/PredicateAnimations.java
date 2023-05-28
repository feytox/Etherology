package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class PredicateAnimations {
    // predicate animations
    public static final PredicatePlayerAnimation CRATE_CARRYING;
    public static final PredicatePlayerAnimation LEFT_HAMMER_IDLE;
    public static final PredicatePlayerAnimation RIGHT_HAMMER_IDLE;

    static {
        CRATE_CARRYING = new PredicatePlayerAnimation(new EIdentifier("crate.carry"), 5, Ease.INOUTCUBIC, true, false,
                player -> player.getMainHandStack().isOf(EItems.CARRIED_CRATE)).register();

//        LEFT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("left_hammer_idle"), 0, null, true, false,
//                player -> AnimationPredicates.twohandedIdle(player, Arm.LEFT),
//                LEFT_HAMMER_HIT, LEFT_HAMMER_HIT_WEAK).register();
//        RIGHT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("right_hammer_idle"), 0, null, true, false,
//                player -> AnimationPredicates.twohandedIdle(player, Arm.RIGHT),
//                RIGHT_HAMMER_HIT, RIGHT_HAMMER_HIT_WEAK).register();

        LEFT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("left_hammer_idle"), 2, Ease.INCUBIC, true, false,
                player -> false).register();
        RIGHT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("right_hammer_idle"), 2, Ease.INCUBIC, true, false,
                player -> false).register();
    }
}

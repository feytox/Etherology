package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.util.Arm;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.feyapi.AnimationPredicates;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class PredicateAnimations {
    // predicate animations
    public static final PredicatePlayerAnimation CRATE_CARRYING = new PredicatePlayerAnimation(new EIdentifier("crate.carry"), 5, Ease.INOUTCUBIC, true, false,
            player -> player.getMainHandStack().isOf(EItems.CARRIED_CRATE));
//    public static final PredicatePlayerAnimation LEFT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("left_hammer_idle"), 0, null, true, false,
//            player -> AnimationPredicates.twohandedIdle(player, Arm.LEFT),
//            new EIdentifier("left_hammer_hit"), new EIdentifier("left_hammer_hit_weak"));

    public static final PredicatePlayerAnimation LEFT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("left_hammer_idle"), 0, null, true, false,
            player -> AnimationPredicates.twohandedIdle(player, Arm.LEFT));

//    public static final PredicatePlayerAnimation RIGHT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("right_hammer_idle"), 0, null, true, false,
//            player -> AnimationPredicates.twohandedIdle(player, Arm.RIGHT),
//            new EIdentifier("right_hammer_hit"), new EIdentifier("right_hammer_hit_weak"));

    public static final PredicatePlayerAnimation RIGHT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("right_hammer_idle"), 0, null, true, false,
            player -> AnimationPredicates.twohandedIdle(player, Arm.RIGHT));

    public static void registerAll() {
        CRATE_CARRYING.register();
        LEFT_HAMMER_IDLE.register();
        RIGHT_HAMMER_IDLE.register();
    }
}

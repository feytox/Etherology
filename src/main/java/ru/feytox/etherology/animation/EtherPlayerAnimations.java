package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.util.Arm;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.feyapi.AnimationPredicates;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EtherPlayerAnimations {
    // predicate animations
    public static final PredicatePlayerAnimation CRATE_CARRYING;
    public static final PredicatePlayerAnimation LEFT_HAMMER_IDLE;
    public static final PredicatePlayerAnimation RIGHT_HAMMER_IDLE;

    // triggerable animations
    public static final TriggerablePlayerAnimation LEFT_HAMMER_HIT;
    public static final TriggerablePlayerAnimation RIGHT_HAMMER_HIT;
    public static final TriggerablePlayerAnimation LEFT_HAMMER_HIT_WEAK;
    public static final TriggerablePlayerAnimation RIGHT_HAMMER_HIT_WEAK;

    static {
        LEFT_HAMMER_HIT = new TriggerablePlayerAnimation(new EIdentifier("left_hammer_hit"), true, true);
        RIGHT_HAMMER_HIT = new TriggerablePlayerAnimation(new EIdentifier("right_hammer_hit"), true, true);
        LEFT_HAMMER_HIT_WEAK = new TriggerablePlayerAnimation(new EIdentifier("left_hammer_hit_weak"), true, true);
        RIGHT_HAMMER_HIT_WEAK = new TriggerablePlayerAnimation(new EIdentifier("right_hammer_hit_weak"), true, true);

        CRATE_CARRYING = new PredicatePlayerAnimation(new EIdentifier("crate.carry"), 5, Ease.INOUTCUBIC, true, false,
                player -> player.getMainHandStack().isOf(EItems.CARRIED_CRATE)).register();
        LEFT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("left_hammer_idle"), 0, null, true, false,
                player -> AnimationPredicates.twohandedIdle(player, Arm.LEFT),
                LEFT_HAMMER_HIT, LEFT_HAMMER_HIT_WEAK).register();
        RIGHT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("right_hammer_idle"), 0, null, true, false,
                player -> AnimationPredicates.twohandedIdle(player, Arm.RIGHT),
                RIGHT_HAMMER_HIT, RIGHT_HAMMER_HIT_WEAK).register();
    }
}

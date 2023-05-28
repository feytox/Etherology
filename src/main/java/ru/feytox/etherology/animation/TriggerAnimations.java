package ru.feytox.etherology.animation;

import ru.feytox.etherology.util.feyapi.EIdentifier;

import static ru.feytox.etherology.animation.PredicateAnimations.LEFT_HAMMER_IDLE;
import static ru.feytox.etherology.animation.PredicateAnimations.RIGHT_HAMMER_IDLE;

public class TriggerAnimations {
    public static final TriggerablePlayerAnimation LEFT_HAMMER_HIT;
    public static final TriggerablePlayerAnimation RIGHT_HAMMER_HIT;
    public static final TriggerablePlayerAnimation LEFT_HAMMER_HIT_WEAK;
    public static final TriggerablePlayerAnimation RIGHT_HAMMER_HIT_WEAK;

    static {
        LEFT_HAMMER_HIT = new TriggerablePlayerAnimation(new EIdentifier("left_hammer_hit"), true, true,
                LEFT_HAMMER_IDLE::play);
        RIGHT_HAMMER_HIT = new TriggerablePlayerAnimation(new EIdentifier("right_hammer_hit"), true, true,
                RIGHT_HAMMER_IDLE::play);
        LEFT_HAMMER_HIT_WEAK = new TriggerablePlayerAnimation(new EIdentifier("left_hammer_hit_weak"), true, true,
                LEFT_HAMMER_IDLE::play);
        RIGHT_HAMMER_HIT_WEAK = new TriggerablePlayerAnimation(new EIdentifier("right_hammer_hit_weak"), true, true,
                RIGHT_HAMMER_IDLE::play);
    }
}

package ru.feytox.etherology.animation;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Arm;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.item.TwoHandheldSword;
import ru.feytox.etherology.util.misc.EIdentifier;

public class PredicateAnimations {

    // hammer + glaive animations
    public static final PredicatePlayerAnimation HAMMER_IDLE_LEFT = new PredicatePlayerAnimation(new EIdentifier("hammer_idle_left"), 0, null, false, false,
            player -> twohandheldIdle(player, Arm.LEFT),
            PlayerAnimationController.setHammerState(HammerState.IDLE),
            new EIdentifier("hammer_idle_right"));
    public static final PredicatePlayerAnimation HAMMER_IDLE_RIGHT = new PredicatePlayerAnimation(new EIdentifier("hammer_idle_right"), 0, null, false, false,
            player -> twohandheldIdle(player, Arm.RIGHT),
            PlayerAnimationController.setHammerState(HammerState.IDLE),
            new EIdentifier("hammer_idle_left"));

    /**
     * Returns the appropriate animation for a two-handed weapon based on the given parameters.
     *
     * @param  player          the player holding the weapon
     * @param  arm             the main arm holding the weapon
     * @return                 the corresponding animation for the given parameters
     */
    private static boolean twohandheldIdle(AbstractClientPlayerEntity player, Arm arm) {
        return TwoHandheldSword.check(player, TwoHandheldSword.class) && player.getMainArm().equals(arm);
    }

    /**
     * Registers all Predicate Animations.
     */
    public static void registerAll() {
        HAMMER_IDLE_LEFT.register();
        HAMMER_IDLE_RIGHT.register();
    }
}

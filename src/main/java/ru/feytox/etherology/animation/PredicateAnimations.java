package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Arm;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.item.TwoHandheldSword;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class PredicateAnimations {
    // predicate animations
    public static final PredicatePlayerAnimation CRATE_CARRYING = new PredicatePlayerAnimation(new EIdentifier("crate.carry"), 5, Ease.INOUTCUBIC, true, false,
            player -> player.getMainHandStack().isOf(EItems.CARRIED_CRATE));

    public static final PredicatePlayerAnimation LEFT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("left_hammer_idle"), 0, null, false, false,
            player -> twohandedIdle(player, Arm.LEFT),
            PlayerAnimationController.setHammerState(HammerState.IDLE));

    public static final PredicatePlayerAnimation RIGHT_HAMMER_IDLE = new PredicatePlayerAnimation(new EIdentifier("right_hammer_idle"), 0, null, false, false,
            player -> twohandedIdle(player, Arm.RIGHT),
            PlayerAnimationController.setHammerState(HammerState.IDLE));

    private static boolean twohandedIdle(AbstractClientPlayerEntity player, Arm arm) {
        return TwoHandheldSword.check(player, TwoHandheldSword.class) && player.getMainArm().equals(arm);
    }

    public static void registerAll() {
        CRATE_CARRYING.register();
        LEFT_HAMMER_IDLE.register();
        RIGHT_HAMMER_IDLE.register();
    }
}

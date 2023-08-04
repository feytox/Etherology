package ru.feytox.etherology.animation.playerAnimation;

import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Arm;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.item.TwoHandheldSword;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class PredicateAnimations {
    // crate animation
    public static final PredicatePlayerAnimation CRATE_CARRYING = new PredicatePlayerAnimation(new EIdentifier("crate.carry"), 5, Ease.INOUTCUBIC, true, false,
            player -> player.getMainHandStack().isOf(EItems.CARRIED_CRATE));

    // hammer + glaive animations
    public static final PredicatePlayerAnimation HAMMER_IDLE_LEFT = new PredicatePlayerAnimation(new EIdentifier("hammer_idle_left"), 0, null, false, false,
            player -> twohandheldIdle(player, Arm.LEFT),
            PlayerAnimationController.setHammerState(HammerState.IDLE),
            new EIdentifier("hammer_idle_right"));
    public static final PredicatePlayerAnimation HAMMER_IDLE_RIGHT = new PredicatePlayerAnimation(new EIdentifier("hammer_idle_right"), 0, null, false, false,
            player -> twohandheldIdle(player, Arm.RIGHT),
            PlayerAnimationController.setHammerState(HammerState.IDLE),
            new EIdentifier("hammer_idle_left"));

    private static boolean twohandheldIdle(AbstractClientPlayerEntity player, Arm arm) {
        return TwoHandheldSword.check(player, TwoHandheldSword.class) && player.getMainArm().equals(arm);
    }

    public static void registerAll() {
        CRATE_CARRYING.register();
        HAMMER_IDLE_LEFT.register();
        HAMMER_IDLE_RIGHT.register();
    }
}

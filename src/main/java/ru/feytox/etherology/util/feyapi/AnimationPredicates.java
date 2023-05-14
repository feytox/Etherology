package ru.feytox.etherology.util.feyapi;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Arm;
import ru.feytox.etherology.item.HammerItem;

public class AnimationPredicates {
    public static boolean twohandedIdle(AbstractClientPlayerEntity player, Arm arm) {
        return player.getMainHandStack().getItem() instanceof HammerItem && player.getMainArm().equals(arm) && player.getOffHandStack().isEmpty();
    }
}

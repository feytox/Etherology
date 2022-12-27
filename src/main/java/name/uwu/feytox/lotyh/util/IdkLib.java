package name.uwu.feytox.lotyh.util;

import name.uwu.feytox.lotyh.components.IFloatComponent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

import static name.uwu.feytox.lotyh.LotyhComponents.ETHER_POINTS;

/**
 * random 'useful' things
 */

public class IdkLib {
    public static boolean isExhaustion(PlayerEntity player) {
        Optional<IFloatComponent> component = ETHER_POINTS.maybeGet(player);
        if (component.isEmpty()) return false;
        return component.get().getValue() < 5;
    }

    public static void tickExhaustion(PlayerEntity player) {
        if (!isExhaustion(player)) return;
        float ether_points = ETHER_POINTS.get(player).getValue();

        if (ether_points < 5 && ether_points >= 2.5F) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 1));
        } else if (ether_points < 2.5F && ether_points >= 1) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 2));
        } else if (ether_points < 1) {
            float multiplier = player.world.getDifficulty().getId() + 1;
            player.damage(DamageSource.MAGIC, multiplier);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 3));
        }
    }
}

package name.uwu.feytox.lotyh.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import static name.uwu.feytox.lotyh.Lotyh.ETHER_EXHAUSTION;
import static name.uwu.feytox.lotyh.LotyhComponents.ETHER_POINTS;

public class ExhaustionEffect extends StatusEffect {
    public ExhaustionEffect() {
        super(StatusEffectCategory.HARMFUL, 0x5F00A0);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            float ether_points = ETHER_POINTS.get(entity).getValue();
            if (ether_points >= 5) {
                entity.removeStatusEffect(ETHER_EXHAUSTION);
            }
            if (ether_points < 5 && ether_points >= 2.5F) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 1));
            } else if (ether_points < 2.5F && ether_points >= 1) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 2));
                entity.addStatusEffect(new StatusEffectInstance(ETHER_EXHAUSTION, 99999, 2));
            } else if (ether_points < 1) {
                entity.addStatusEffect(new StatusEffectInstance(ETHER_EXHAUSTION, 99999, 3));
                float multiplier = entity.world.getDifficulty().getId() + 1;
                entity.damage(DamageSource.MAGIC, multiplier);
            }
        }
    }
}

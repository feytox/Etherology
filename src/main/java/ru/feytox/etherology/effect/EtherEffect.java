package ru.feytox.etherology.effect;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import ru.feytox.etherology.magic.ether.EtherComponent;

public class EtherEffect extends StatusEffect {

    private static final int UPDATE_COOLDOWN = 60;
    private final float baseChange;

    public EtherEffect(StatusEffectCategory category, int color, float baseChange) {
        super(category, color);
        this.baseChange = baseChange;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int cooldown = UPDATE_COOLDOWN >> amplifier;
        return cooldown == 0 || duration % cooldown == 0;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (baseChange >= 0) EtherComponent.increment(entity, baseChange * (1 << amplifier));
        else EtherComponent.decrement(entity, -baseChange * (1 << amplifier));
        return true;
    }
}

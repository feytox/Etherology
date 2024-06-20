package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

@UtilityClass
public class EFoodComponents {

    public static final FoodComponent CRUMB = new FoodComponent.Builder().hunger(2).saturationModifier(0.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600), 0.3f).build();

    public static final FoodComponent COOKED_CRUMB = new FoodComponent.Builder().hunger(5).saturationModifier(6).build();
}

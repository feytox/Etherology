package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.item.FoodComponent;

@UtilityClass
public class EFoodComponents {

    public static final FoodComponent CRUMB = new FoodComponent.Builder().hunger(5).saturationModifier(6).build();
}

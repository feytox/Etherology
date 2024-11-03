package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.component.type.FoodComponent;

@UtilityClass
public class EFoodComponents {

    public static final FoodComponent CRUMB = new FoodComponent.Builder().nutrition(3).saturationModifier(6).build();
}

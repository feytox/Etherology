package ru.feytox.etherology.util.feyapi;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@UtilityClass
public class ItemUtils {

    public static boolean isUsingItem(LivingEntity entity, Class<? extends Item> itemClass) {
        val items = entity.getHandItems();
        for (ItemStack stack : items) {
            if (itemClass.isInstance(stack.getItem())) return true;
        }
        return false;
    }
}

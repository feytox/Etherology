package ru.feytox.etherology.util.misc;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import ru.feytox.etherology.Etherology;

@UtilityClass
public class ItemUtils {

    public static boolean isUsingItem(LivingEntity entity, Class<? extends Item> itemClass) {
        val items = entity.getHandItems();
        for (ItemStack stack : items) {
            if (itemClass.isInstance(stack.getItem())) return true;
        }
        return false;
    }

    /**
     * This method is used in cases when there is no other way to obtain "ServerWorld".
     * The method attempts to get "ServerWorld" and applies normal damage, but if it is not found, it just damages the item.
     */
    public static void damage(ItemStack stack, int amount) {
        ServerWorld world = Etherology.getAnyServerWorld();
        if (world != null) {
            stack.damage(amount, world, null, item -> {});
            return;
        }

        int damage = stack.getDamage() + amount;
        stack.setDamage(damage);
        if (damage >= stack.getMaxDamage()) {
            stack.decrement(1);
        }
    }
}

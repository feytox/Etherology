package ru.feytox.etherology.item;

import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public abstract class TwoHandheldSword extends SwordItem {

    public TwoHandheldSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public static <T extends TwoHandheldSword> boolean check(PlayerEntity player, Class<T> cls) {
        return cls.isInstance(player.getMainHandStack().getItem()) && player.getOffHandStack().isEmpty();
    }

    public static boolean isUsing(LivingEntity entity) {
        for (val stack : entity.getHandItems()) {
            if (stack.getItem() instanceof TwoHandheldSword) return true;
        }
        return false;
    }

    public static boolean hideExtraItem(LivingEntity entity, ItemStack stack) {
        return TwoHandheldSword.isUsing(entity) && !(stack.getItem() instanceof TwoHandheldSword);
    }
}

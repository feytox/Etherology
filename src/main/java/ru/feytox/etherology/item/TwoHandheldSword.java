package ru.feytox.etherology.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public abstract class TwoHandheldSword extends SwordItem {

    public TwoHandheldSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        this(toolMaterial, settings.attributeModifiers(SwordItem.createAttributeModifiers(toolMaterial, attackDamage, attackSpeed)));
    }

    public TwoHandheldSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    public static <T extends TwoHandheldSword> boolean isUsing(PlayerEntity player, Class<T> cls) {
        return cls.isInstance(player.getMainHandStack().getItem());
    }

    public static boolean hideExtraItem(LivingEntity entity, ItemStack stack) {
        var mainStack = entity.getMainHandStack();
        var offStack = entity.getOffHandStack();

        if (!(mainStack.getItem() instanceof TwoHandheldSword) && !(offStack.getItem() instanceof TwoHandheldSword))
            return false;

        if (!(stack.getItem() instanceof TwoHandheldSword))
            return true;

        return !mainStack.equals(stack);
    }
}

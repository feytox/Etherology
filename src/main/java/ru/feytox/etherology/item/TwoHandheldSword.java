package ru.feytox.etherology.item;

import lombok.val;
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

    private static boolean isInHands(LivingEntity entity) {
        for (val stack : entity.getHandItems()) {
            if (stack.getItem() instanceof TwoHandheldSword) return true;
        }
        return false;
    }

    public static boolean hideExtraItem(LivingEntity entity, ItemStack stack) {
        return TwoHandheldSword.isInHands(entity) && !(stack.getItem() instanceof TwoHandheldSword);
    }
}

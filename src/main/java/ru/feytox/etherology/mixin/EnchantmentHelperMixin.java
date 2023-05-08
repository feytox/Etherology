package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.feytox.etherology.item.BattlePickaxe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean onCheck(EnchantmentTarget instance, Item item) {
        return instance.equals(EnchantmentTarget.WEAPON) && item instanceof BattlePickaxe || instance.isAcceptableItem(item);
    }

    @Redirect(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;"))
    private static List<EnchantmentLevelEntry> onGetEntries(int power, ItemStack stack, boolean treasureAllowed) {
        List<EnchantmentLevelEntry> enchantments = EnchantmentHelper.getPossibleEntries(power, stack, treasureAllowed);
        if (!(stack.getItem() instanceof BattlePickaxe)) return enchantments;

        return new ArrayList<>(enchantments).stream()
                .filter(entry -> !entry.enchantment.equals(Enchantments.FORTUNE) && !entry.enchantment.equals(Enchantments.SILK_TOUCH))
                .collect(Collectors.toList());
    }
}

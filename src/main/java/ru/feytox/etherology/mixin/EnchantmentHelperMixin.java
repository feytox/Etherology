package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.util.feyapi.EtherEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean onCheck(EnchantmentTarget instance, Item item) {
        return instance.equals(EnchantmentTarget.WEAPON) && item instanceof BattlePickaxe || instance.isAcceptableItem(item);
    }

    @Redirect(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;"))
    private static List<EnchantmentLevelEntry> onGetEntries(int power, ItemStack stack, boolean treasureAllowed) {
        List<EnchantmentLevelEntry> enchantments = EnchantmentHelper.getPossibleEntries(power, stack, treasureAllowed);
        List<Enchantment> banned = EtherEnchantments.getBannedEnchantments(stack);
        if (banned == null) return enchantments;

        return new ArrayList<>(enchantments).stream()
                .filter(entry -> !banned.contains(entry.enchantment))
                .collect(Collectors.toList());
    }
}

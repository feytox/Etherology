package ru.feytox.etherology.util.feyapi;

import com.google.common.collect.ImmutableList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.HammerItem;

import java.util.List;
import java.util.Map;

public class EtherEnchantments {
    private static final Map<Class<?>, List<Enchantment>> bannedEnchantments = Map.of(
            BattlePickaxe.class, ImmutableList.of(Enchantments.FORTUNE, Enchantments.SILK_TOUCH),
            HammerItem.class, ImmutableList.of(Enchantments.SHARPNESS, Enchantments.LOOTING, Enchantments.FIRE_ASPECT)
    );

    @Nullable
    public static List<Enchantment> getBannedEnchantments(ItemStack stack) {
        return bannedEnchantments.getOrDefault(stack.getItem().getClass(), null);
    }
}

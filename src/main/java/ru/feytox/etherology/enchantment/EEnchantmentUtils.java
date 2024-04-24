package ru.feytox.etherology.enchantment;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.GlaiveItem;
import ru.feytox.etherology.item.glints.GlintItem;

import java.util.List;
import java.util.Map;

public class EEnchantmentUtils {

    @Getter
    private static boolean battlePickWeaponMatched = true;

    private static final Map<Class<?>, List<Enchantment>> bannedEnchantments = Map.of(
            BattlePickaxe.class, ImmutableList.of(Enchantments.FORTUNE, Enchantments.SILK_TOUCH),
            GlaiveItem.class, ImmutableList.of(Enchantments.LOOTING),
            GlintItem.class, ImmutableList.of(Enchantments.UNBREAKING, Enchantments.MENDING, Enchantments.VANISHING_CURSE)
    );

    @Nullable
    public static List<Enchantment> getBannedEnchantments(Item item) {
        return bannedEnchantments.getOrDefault(item.getClass(), null);
    }

    public static void markBattlePickNotMatched() {
        battlePickWeaponMatched = false;
    }
}

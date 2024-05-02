package ru.feytox.etherology.enchantment;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.GlaiveItem;
import ru.feytox.etherology.item.TuningMaceItem;
import ru.feytox.etherology.item.glints.GlintItem;

import java.util.List;
import java.util.Map;

import static net.minecraft.enchantment.Enchantments.*;

@UtilityClass
public class EEnchantmentUtils {

    @Getter
    private static boolean battlePickWeaponMatched = true;
    private static final Map<Class<? extends Item>, List<Enchantment>> bannedEnchantments = new Object2ObjectOpenHashMap<>();

    public static void registerBannedEnchantments() {
        register(BattlePickaxe.class, FORTUNE, SILK_TOUCH);
        register(GlaiveItem.class, LOOTING);
        register(GlintItem.class, UNBREAKING, MENDING, VANISHING_CURSE);
        register(TuningMaceItem.class, SHARPNESS, FIRE_ASPECT, LOOTING, SWEEPING);
    }

    private static void register(Class<? extends Item> itemClass, Enchantment... enchantments) {
        bannedEnchantments.put(itemClass, new ObjectArrayList<>(enchantments));
    }

    @Nullable
    public static List<Enchantment> getBannedEnchantments(Item item) {
        return bannedEnchantments.getOrDefault(item.getClass(), null);
    }

    public static void markBattlePickNotMatched() {
        battlePickWeaponMatched = false;
    }
}

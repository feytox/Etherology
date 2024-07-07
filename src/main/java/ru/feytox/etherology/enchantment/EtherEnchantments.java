package ru.feytox.etherology.enchantment;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.BroadSwordItem;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.TuningMaceItem;
import ru.feytox.etherology.item.glints.GlintItem;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.enchantment.Enchantments.*;

@UtilityClass
public class EtherEnchantments {

    @Setter @Getter
    private static boolean battlePickWeaponMatched = true;
    private static final Map<Class<? extends Item>, List<Enchantment>> bannedEnchantments = new Object2ObjectOpenHashMap<>();

    public static final Enchantment PEAL = register("peal", new PealEnchantment());
    public static final Enchantment REFLECTION = register("reflection", new ReflectionEnchantment());

    public static void registerAll() {
        registerBannedEnchantments();
    }

    private Enchantment register(String id, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, EIdentifier.of(id), enchantment);
    }

    private static void registerBannedEnchantments() {
        banEnchant(BattlePickaxe.class, FORTUNE, SILK_TOUCH);
        banEnchant(BroadSwordItem.class, LOOTING);
        banEnchant(GlintItem.class, UNBREAKING, MENDING, VANISHING_CURSE);
        banEnchant(TuningMaceItem.class, SHARPNESS, FIRE_ASPECT, LOOTING, SWEEPING_EDGE);
        banEnchant(LensItem.class, UNBREAKING, MENDING, VANISHING_CURSE);
    }

    private static void banEnchant(Class<? extends Item> itemClass, Enchantment... enchantments) {
        bannedEnchantments.put(itemClass, new ObjectArrayList<>(enchantments));
    }

    @Nullable
    public static List<Enchantment> getBannedEnchantments(Item item) {
        return bannedEnchantments.getOrDefault(item.getClass(), null);
    }
}

package ru.feytox.etherology.enchantment;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
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

    public static void registerAll() {
        registerBannedEnchantments();

        register("reflection", ReflectionEnchantment.INSTANCE);
        register("peal", PealEnchantment.INSTANCE);
    }

    private void register(String id, Supplier<? extends Enchantment> enchantSupplier) {
        Registry.register(Registries.ENCHANTMENT, new EIdentifier(id), enchantSupplier.get());
    }

    private static void registerBannedEnchantments() {
        banEnchant(BattlePickaxe.class, FORTUNE, SILK_TOUCH);
        banEnchant(BroadSwordItem.class, LOOTING);
        banEnchant(GlintItem.class, UNBREAKING, MENDING, VANISHING_CURSE);
        banEnchant(TuningMaceItem.class, SHARPNESS, FIRE_ASPECT, LOOTING, SWEEPING);
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

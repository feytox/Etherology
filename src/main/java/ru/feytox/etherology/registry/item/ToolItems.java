package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.item.*;
import ru.feytox.etherology.util.misc.EIdentifier;

import static net.minecraft.item.ToolMaterials.*;
import static ru.feytox.etherology.registry.util.EtherToolMaterials.EBONY;
import static ru.feytox.etherology.registry.util.EtherToolMaterials.ETHRIL;

@UtilityClass
public class ToolItems {
    // ethril tools
    public static final Item ETHRIL_AXE = register("ethril_axe", new AxeItem(ETHRIL, 5, -3.0f, new FabricItemSettings()));
    public static final Item ETHRIL_PICKAXE = register("ethril_pickaxe", new PickaxeItem(ETHRIL, 1, -2.8F, new FabricItemSettings()));
    public static final Item ETHRIL_HOE = register("ethril_hoe", new HoeItem(ETHRIL, -3, 0.0f, new FabricItemSettings()));
    public static final Item ETHRIL_SHOVEL = register("ethril_shovel", new ShovelItem(ETHRIL, 1.5f, -3.0f, new FabricItemSettings()));
    public static final Item ETHRIL_SWORD = register("ethril_sword", new SwordItem(ETHRIL, 3, -2.4f, new FabricItemSettings()));

    // ebony tools
    public static final Item EBONY_AXE = register("ebony_axe", new AxeItem(EBONY, 5, -3.1F, new FabricItemSettings()));
    public static final Item EBONY_PICKAXE = register("ebony_pickaxe", new PickaxeItem(EBONY, 0, -2.8F, new FabricItemSettings()));
    public static final Item EBONY_HOE = register("ebony_hoe", new HoeItem(EBONY, -2, -1, new FabricItemSettings()));
    public static final Item EBONY_SHOVEL = register("ebony_shovel", new ShovelItem(EBONY, 0.5f, -3, new FabricItemSettings()));
    public static final Item EBONY_SWORD = register("ebony_sword", new SwordItem(EBONY, 3, -2.4f, new FabricItemSettings()));

    // battle pickaxes
    public static final Item WOODEN_BATTLE_PICKAXE = register("wooden_battle_pickaxe", new BattlePickaxe(WOOD, 2, -2.6f));
    public static final Item STONE_BATTLE_PICKAXE = register("stone_battle_pickaxe", new BattlePickaxe(STONE, 2, -2.6f));
    public static final Item IRON_BATTLE_PICKAXE = register("iron_battle_pickaxe", new BattlePickaxe(IRON, 2, -2.6f));
    public static final Item GOLDEN_BATTLE_PICKAXE = register("golden_battle_pickaxe", new BattlePickaxe(GOLD, 2, -2.6f));
    public static final Item ETHRIL_BATTLE_PICKAXE = register("ethril_battle_pickaxe", new BattlePickaxe(ETHRIL, 1, -2.6f));
    public static final Item EBONY_BATTLE_PICKAXE = register("ebony_battle_pickaxe", new BattlePickaxe(EBONY, 1, -2.6f));
    public static final Item DIAMOND_BATTLE_PICKAXE = register("diamond_battle_pickaxe", new BattlePickaxe(DIAMOND, 2, -2.6f));
    public static final Item NETHERITE_BATTLE_PICKAXE = register("netherite_battle_pickaxe", new BattlePickaxe(NETHERITE, 2, -2.6f, new FabricItemSettings().fireproof()));
    public static final Item[] BATTLE_PICKAXES = {WOODEN_BATTLE_PICKAXE, STONE_BATTLE_PICKAXE, IRON_BATTLE_PICKAXE, GOLDEN_BATTLE_PICKAXE, ETHRIL_BATTLE_PICKAXE, EBONY_BATTLE_PICKAXE, DIAMOND_BATTLE_PICKAXE, NETHERITE_BATTLE_PICKAXE};

    // combat tools
    public static final Item IRON_SHIELD = register("iron_shield", new EtherShield(new FabricItemSettings().maxDamage(452), 140, 14, Items.IRON_INGOT));
    public static final Item TUNING_MACE = register("tuning_mace", new TuningMaceItem());
    public static final Item BROADSWORD = register("broadsword", new BroadSwordItem());

    // single tools
    public static final Item OCULUS = register("oculus", new OculusItem());
    public static final Item STAFF = register("staff", new StaffItem());
    public static final Item STREAM_KEY = register("stream_key", new StreamKeyItem());

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new EIdentifier(id), item);
    }

    public static void registerAll() {}
}

package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.item.*;
import ru.feytox.etherology.util.feyapi.EIdentifier;

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

    // hammers
    public static final Item WOODEN_HAMMER = register("wooden_hammer", new HammerItem(WOOD, 9, -3.7f));
    public static final Item STONE_HAMMER = register("stone_hammer", new HammerItem(STONE, 8, -3.7f));
    public static final Item IRON_HAMMER = register("iron_hammer", new HammerItem(IRON, 9, -3.6f));
    public static final Item GOLDEN_HAMMER = register("golden_hammer", new HammerItem(GOLD, 9, -3.6f));
    public static final Item ETHRIL_HAMMER = register("ethril_hammer", new HammerItem(ETHRIL, 8, -3.6f));
    public static final Item EBONY_HAMMER = register("ebony_hammer", new HammerItem(EBONY, 8, -3.5f));
    public static final Item DIAMOND_HAMMER = register("diamond_hammer", new HammerItem(DIAMOND, 8, -3.5f));
    public static final Item NETHERITE_HAMMER = register("netherite_hammer", new HammerItem(NETHERITE, 7, -3.5f, new FabricItemSettings().fireproof()));
    public static final Item[] HAMMERS = {WOODEN_HAMMER, STONE_HAMMER, IRON_HAMMER, GOLDEN_HAMMER, ETHRIL_HAMMER, EBONY_HAMMER, DIAMOND_HAMMER, NETHERITE_HAMMER};

    // glaives
    public static final Item WOODEN_GLAIVE = register("wooden_glaive", new GlaiveItem(WOOD, 1, -3.2f));
    public static final Item STONE_GLAIVE = register("stone_glaive", new GlaiveItem(STONE, 1, -3.2f));
    public static final Item IRON_GLAIVE = register("iron_glaive", new GlaiveItem(IRON, 1, -3.1f));
    public static final Item GOLDEN_GLAIVE = register("golden_glaive", new GlaiveItem(GOLD, 1, -3.0f));
    public static final Item ETHRIL_GLAIVE = register("ethril_glaive", new GlaiveItem(ETHRIL, 1, -3.0f));
    public static final Item EBONY_GLAIVE = register("ebony_glaive", new GlaiveItem(EBONY, 1, -3.1f));
    public static final Item DIAMOND_GLAIVE = register("diamond_glaive", new GlaiveItem(DIAMOND, 1, -3.0f));
    public static final Item NETHERITE_GLAIVE = register("netherite_glaive", new GlaiveItem(NETHERITE, 1, -3.0f, new FabricItemSettings().fireproof()));
    public static final Item[] GLAIVES = {WOODEN_GLAIVE, STONE_GLAIVE, IRON_GLAIVE, GOLDEN_GLAIVE, ETHRIL_GLAIVE, EBONY_GLAIVE, DIAMOND_GLAIVE, NETHERITE_GLAIVE};

    // custom shields
    public static final Item IRON_SHIELD = register("iron_shield", new EtherShield(new FabricItemSettings().maxDamage(452), 140, 14, Items.IRON_INGOT));

    // single tools
    public static final Item OCULUS = register("oculus", new OculusItem());
    public static final Item STAFF = register("staff", new StaffItem());
    public static final Item STREAM_KEY = register("stream_key", new StreamKeyItem());

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new EIdentifier(id), item);
    }

    public static void registerAll() {}
}

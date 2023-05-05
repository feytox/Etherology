package ru.feytox.etherology.registry.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import static ru.feytox.etherology.registry.util.EtherToolMaterials.ETHRIL;
import static ru.feytox.etherology.registry.util.EtherToolMaterials.TELDER_STEEL;

public class ToolItems {
    public static Item ETHRIL_AXE = register("ethril_axe", new AxeItem(ETHRIL, 5, -3, new FabricItemSettings()));
    public static Item ETHRIL_PICKAXE = register("ethril_pickaxe", new PickaxeItem(ETHRIL, 0, -2.8F, new FabricItemSettings()));
    public static Item ETHRIL_HOE = register("ethril_hoe", new HoeItem(ETHRIL, 0, -3, new FabricItemSettings()));
    public static Item ETHRIL_SHOVEL = register("ethril_shovel", new ShovelItem(ETHRIL, 0.5f, -3, new FabricItemSettings()));
    public static Item ETHRIL_SWORD = register("ethril_sword", new SwordItem(ETHRIL, 3, -2.4f, new FabricItemSettings()));

    public static Item TELDER_STEEL_AXE = register("telder_steel_axe", new AxeItem(TELDER_STEEL, 5, -3.1F, new FabricItemSettings()));
    public static Item TELDER_STEEL_PICKAXE = register("telder_steel_pickaxe", new PickaxeItem(TELDER_STEEL, 0, -2.8F, new FabricItemSettings()));
    public static Item TELDER_STEEL_HOE = register("telder_steel_hoe", new HoeItem(TELDER_STEEL, -2, -1, new FabricItemSettings()));
    public static Item TELDER_STEEL_SHOVEL = register("telder_steel_shovel", new ShovelItem(TELDER_STEEL, 0.5f, -3, new FabricItemSettings()));
    public static Item TELDER_STEEL_SWORD = register("telder_steel_sword", new SwordItem(TELDER_STEEL, 3, -2.4f, new FabricItemSettings()));

    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new EIdentifier(id), item);
    }

    public static void registerAll() {}
}

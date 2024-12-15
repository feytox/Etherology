package ru.feytox.etherology.client.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import ru.feytox.etherology.client.block.closet.ClosetScreen;
import ru.feytox.etherology.client.block.crate.CrateScreen;
import ru.feytox.etherology.client.block.empowerTable.EmpowerTableScreen;
import ru.feytox.etherology.client.block.etherealFurnace.EtherealFurnaceScreen;
import ru.feytox.etherology.client.block.etherealStorage.EtherealStorageScreen;
import ru.feytox.etherology.client.block.inventorTable.InventorTableScreen;
import ru.feytox.etherology.client.block.jewelryTable.JewelryTableScreen;
import ru.feytox.etherology.registry.misc.ScreenHandlersRegistry;

@UtilityClass
public class ScreenRegistry {

    public static void register() {
        HandledScreens.register(ScreenHandlersRegistry.CLOSET_SCREEN_HANDLER, ClosetScreen::new);
        HandledScreens.register(ScreenHandlersRegistry.ETHEREAL_STORAGE_SCREEN_HANDLER, EtherealStorageScreen::new);
        HandledScreens.register(ScreenHandlersRegistry.ETHEREAL_FURNACE_SCREEN_HANDLER, EtherealFurnaceScreen::new);
        HandledScreens.register(ScreenHandlersRegistry.EMPOWER_TABLE_SCREEN_HANDLER, EmpowerTableScreen::new);
        HandledScreens.register(ScreenHandlersRegistry.CRATE_SCREEN_HANDLER, CrateScreen::new);
        HandledScreens.register(ScreenHandlersRegistry.INVENTOR_TABLE_SCREEN_HANDLER, InventorTableScreen::new);
        HandledScreens.register(ScreenHandlersRegistry.JEWELRY_TABLE_SCREEN_HANDLER, JewelryTableScreen::new);
    }
}

package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import ru.feytox.etherology.block.closet.ClosetScreen;
import ru.feytox.etherology.block.closet.ClosetScreenHandler;
import ru.feytox.etherology.block.crate.CrateScreen;
import ru.feytox.etherology.block.crate.CrateScreenHandler;
import ru.feytox.etherology.block.empowerTable.EmpowerTableScreen;
import ru.feytox.etherology.block.empowerTable.EmpowerTableScreenHandler;
import ru.feytox.etherology.block.etherealFurnace.EtherealFurnaceScreen;
import ru.feytox.etherology.block.etherealFurnace.EtherealFurnaceScreenHandler;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageScreen;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageScreenHandler;
import ru.feytox.etherology.block.inventorTable.InventorTableScreen;
import ru.feytox.etherology.block.inventorTable.InventorTableScreenHandler;
import ru.feytox.etherology.block.jewelryTable.JewelryTableScreen;
import ru.feytox.etherology.block.jewelryTable.JewelryTableScreenHandler;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class ScreenHandlersRegistry {

    public static final ScreenHandlerType<ClosetScreenHandler> CLOSET_SCREEN_HANDLER = new ScreenHandlerType<>(ClosetScreenHandler::new);
    public static final ScreenHandlerType<EtherealStorageScreenHandler> ETHEREAL_STORAGE_SCREEN_HANDLER = new ScreenHandlerType<>(EtherealStorageScreenHandler::new);
    public static final ScreenHandlerType<EtherealFurnaceScreenHandler> ETHEREAL_FURNACE_SCREEN_HANDLER = new ScreenHandlerType<>(EtherealFurnaceScreenHandler::new);
    public static final ScreenHandlerType<EmpowerTableScreenHandler> EMPOWER_TABLE_SCREEN_HANDLER = new ScreenHandlerType<>(EmpowerTableScreenHandler::new);
    public static final ScreenHandlerType<CrateScreenHandler> CRATE_SCREEN_HANDLER = new ScreenHandlerType<>(CrateScreenHandler::new);
    public static final ScreenHandlerType<InventorTableScreenHandler> INVENTOR_TABLE_SCREEN_HANDLER = new ScreenHandlerType<>(InventorTableScreenHandler::new);
    public static final ScreenHandlerType<JewelryTableScreenHandler> JEWELRY_TABLE_SCREEN_HANDLER = new ScreenHandlerType<>(JewelryTableScreenHandler::new);

    public static void registerServerSide() {
        registerHandler("closet_screen_handler", CLOSET_SCREEN_HANDLER);
        registerHandler("ethereal_storage_screen_handler", ETHEREAL_STORAGE_SCREEN_HANDLER);
        registerHandler("ethereal_furnace_screen_handler", ETHEREAL_FURNACE_SCREEN_HANDLER);
        registerHandler("empower_table_screen_handler", EMPOWER_TABLE_SCREEN_HANDLER);
        registerHandler("crate_screen_handler", CRATE_SCREEN_HANDLER);
        registerHandler("inventor_table_screen_handler", INVENTOR_TABLE_SCREEN_HANDLER);
        registerHandler("jewelry_table_screen_handler", JEWELRY_TABLE_SCREEN_HANDLER);
    }

    public static void registerClientSide() {
        HandledScreens.register(CLOSET_SCREEN_HANDLER, ClosetScreen::new);
        HandledScreens.register(ETHEREAL_STORAGE_SCREEN_HANDLER, EtherealStorageScreen::new);
        HandledScreens.register(ETHEREAL_FURNACE_SCREEN_HANDLER, EtherealFurnaceScreen::new);
        HandledScreens.register(EMPOWER_TABLE_SCREEN_HANDLER, EmpowerTableScreen::new);
        HandledScreens.register(CRATE_SCREEN_HANDLER, CrateScreen::new);
        HandledScreens.register(INVENTOR_TABLE_SCREEN_HANDLER, InventorTableScreen::new);
        HandledScreens.register(JEWELRY_TABLE_SCREEN_HANDLER, JewelryTableScreen::new);
    }

    private static void registerHandler(String id, ScreenHandlerType<?> screenHandlerType) {
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier(id), screenHandlerType);
    }
}

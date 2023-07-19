package ru.feytox.etherology.registry.util;

import io.wispforest.owo.ui.hud.Hud;
import lombok.experimental.UtilityClass;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class GuiRegistry {

    public static void registerAll() {
        registerHUD();
    }

    private static void registerHUD() {
        Hud.add(new EIdentifier("oculus_hud"), OculusItem::initHud);
    }
}

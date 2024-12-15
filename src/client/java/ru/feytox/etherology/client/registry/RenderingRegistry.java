package ru.feytox.etherology.client.registry;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.wispforest.owo.ui.hud.Hud;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import ru.feytox.etherology.client.gui.staff.StaffIndicator;
import ru.feytox.etherology.client.item.OculusItemClient;
import ru.feytox.etherology.client.item.revelationView.RevelationViewItemRenderer;
import ru.feytox.etherology.registry.item.ArmorItems;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class RenderingRegistry {

    public static void registerAll() {
        registerHUD();
        TrinketRendererRegistry.registerRenderer(ArmorItems.REVELATION_VIEW, new RevelationViewItemRenderer());
    }

    private static void registerHUD() {
        Hud.add(EIdentifier.of("oculus_hud"), OculusItemClient::initHud);
        HudRenderCallback.EVENT.register(StaffIndicator::renderHud);
    }
}

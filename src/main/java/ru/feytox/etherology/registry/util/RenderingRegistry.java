package ru.feytox.etherology.registry.util;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.wispforest.owo.ui.hud.Hud;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import ru.feytox.etherology.entity.feature.RedstoneRayRenderer;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.registry.item.ArmorItems;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
@Environment(EnvType.CLIENT)
public class RenderingRegistry {

    public static void registerAll() {
        registerHUD();
        TrinketRendererRegistry.registerRenderer(ArmorItems.REVELATION_VIEW, (TrinketRenderer) ArmorItems.REVELATION_VIEW);

        WorldRenderEvents.END.register(RedstoneRayRenderer::render);
    }

    private static void registerHUD() {
        Hud.add(new EIdentifier("oculus_hud"), OculusItem::initHud);
    }
}

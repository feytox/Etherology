package ru.feytox.etherology;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.feytox.etherology.gui.staff.StaffIndicator;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.model.EtherologyModelProvider;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.registry.entity.EntityRegistry;
import ru.feytox.etherology.registry.item.ModelPredicates;
import ru.feytox.etherology.registry.misc.*;
import ru.feytox.etherology.registry.particle.ClientParticleRegistry;
import ru.feytox.etherology.util.delayedTask.ClientTaskManager;

@Environment(EnvType.CLIENT)
public class EtherologyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GeckoLibNetwork.registerClientReceiverPackets();
        ClientParticleRegistry.registerAll();
        ModelPredicates.registerAll();
        RenderingRegistry.registerAll();
        EtherologyModelProvider.register();
        BlockRenderersRegistry.registerAll();
        ScreenHandlersRegistry.registerClientSide();
        BlockRenderLayerMapRegistry.registerAll();
        ColorProvidersRegistry.registerAll();
        KeybindsRegistry.registerAll();
        EntityRegistry.registerClientSide();
        EventsRegistry.registerClientSide();
        EtherologyNetwork.registerClientSide();

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            ClientTaskManager.INSTANCE.tickTasks();

            StaffIndicator.tickHudData(client);

            // TODO: 12.04.2024 simplify
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (!OculusItem.isUsing(player)) OculusItem.getDisplayedHud().clearChildren();
        }));
    }
}

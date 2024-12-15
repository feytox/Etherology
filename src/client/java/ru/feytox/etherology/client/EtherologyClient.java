package ru.feytox.etherology.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.feytox.etherology.client.block.ClientBlockTickers;
import ru.feytox.etherology.client.block.seal.SealBlockRenderer;
import ru.feytox.etherology.client.gui.staff.StaffIndicator;
import ru.feytox.etherology.client.item.OculusItemClient;
import ru.feytox.etherology.client.model.EtherologyModelProvider;
import ru.feytox.etherology.client.network.EtherologyNetworkClient;
import ru.feytox.etherology.client.registry.*;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.util.delayedTask.ClientTaskManager;
import ru.feytox.etherology.util.misc.EtherProxy;

@Environment(EnvType.CLIENT)
public class EtherologyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EtherProxy.setProxy(new ClientEtherProxy());

        ClientParticleRegistry.registerAll();
        ModelPredicates.registerAll();
        RenderingRegistry.registerAll();
        EtherologyModelProvider.register();
        BlockRenderersRegistry.registerAll();
        ScreenRegistry.register();
        BlockRenderLayerMapRegistry.registerAll();
        ColorProvidersRegistry.registerAll();
        KeybindsRegistry.registerAll();
        EntityClientRegistry.register();
        EventsClientRegistry.register();
        EtherologyNetworkClient.registerAll();
        ClientBlockTickers.addAll();

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            ClientTaskManager.INSTANCE.tickTasks();

            StaffIndicator.tickHudData(client);
            SealBlockRenderer.tickDataResetting(client);

            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (!OculusItem.isInHands(player))
                OculusItemClient.getDisplayedHud().clearChildren();
        }));
    }
}

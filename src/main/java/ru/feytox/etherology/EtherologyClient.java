package ru.feytox.etherology;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.model.EtherologyModelProvider;
import ru.feytox.etherology.registry.entity.EntityRegistry;
import ru.feytox.etherology.registry.item.ModelPredicates;
import ru.feytox.etherology.registry.particle.ClientParticleTypes;
import ru.feytox.etherology.registry.util.*;
import ru.feytox.etherology.util.delayedTask.ClientTaskManager;
import software.bernie.geckolib.network.GeckoLibNetwork;

@Environment(EnvType.CLIENT)
public class EtherologyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GeckoLibNetwork.registerClientReceiverPackets();
        ClientParticleTypes.registerAll();
        ModelPredicates.registerAll();
        GuiRegistry.registerAll();
        EtherologyModelProvider.register();
        BlockRenderersRegistry.registerAll();
        ScreenHandlersRegistry.registerClientSide();
        BlockRenderLayerMapRegistry.registerAll();
        ColorProvidersRegistry.registerAll();
        KeybindsRegistry.registerAll();
        EntityRegistry.registerClientSide();

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            ClientTaskManager.INSTANCE.tickTasks();

            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (!OculusItem.isUsingOculus(player)) OculusItem.getDisplayedHud().clearChildren();
        }));
    }
}

package ru.feytox.etherology.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.StopBlockAnimS2C;
import ru.feytox.etherology.network.animation.SwitchBlockAnimS2C;
import ru.feytox.etherology.network.interaction.RedstoneLensStreamS2C;
import ru.feytox.etherology.network.interaction.RemoveBlockEntityS2C;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

public class EtherologyNetworkClient {

    public static void registerAll() {
        // animation
        registerHandlerS2C(StartBlockAnimS2C.ID, S2CHandlers::receiveStartBlockAnim);
        registerHandlerS2C(StopBlockAnimS2C.ID, S2CHandlers::receiveStopBlockAnim);
        registerHandlerS2C(SwitchBlockAnimS2C.ID, S2CHandlers::receiveSwitchBlockAnim);

        // interaction
        registerHandlerS2C(RedstoneLensStreamS2C.ID, S2CHandlers::receiveRedstoneStream);
        registerHandlerS2C(RemoveBlockEntityS2C.ID, S2CHandlers::receiveRemoveBlockEntity);
    }

    private static <T extends AbstractS2CPacket> void registerHandlerS2C(CustomPayload.Id<T> id, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
    }
}

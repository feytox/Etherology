package ru.feytox.etherology.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.Stop2BlockAnimS2C;
import ru.feytox.etherology.network.animation.StopBlockAnimS2C;
import ru.feytox.etherology.network.animation.SwitchBlockAnimS2C;
import ru.feytox.etherology.network.interaction.RedstoneLensStreamS2C;
import ru.feytox.etherology.network.interaction.RemoveBlockEntityS2C;
import ru.feytox.etherology.network.interaction.StaffMenuSelectionC2S;
import ru.feytox.etherology.network.interaction.StaffTakeLensC2S;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

public class EtherologyNetwork {

    public static void registerCommonSide() {
        // animation
        registerS2C(StartBlockAnimS2C.ID, StartBlockAnimS2C.CODEC);
        registerS2C(Stop2BlockAnimS2C.ID, Stop2BlockAnimS2C.CODEC);
        registerS2C(StopBlockAnimS2C.ID, StopBlockAnimS2C.CODEC);
        registerS2C(SwitchBlockAnimS2C.ID, SwitchBlockAnimS2C.CODEC);

        // interaction
        registerS2C(RedstoneLensStreamS2C.ID, RedstoneLensStreamS2C.CODEC);
        registerS2C(RemoveBlockEntityS2C.ID, RemoveBlockEntityS2C.CODEC);
        registerC2S(StaffMenuSelectionC2S.ID, StaffMenuSelectionC2S.CODEC, StaffMenuSelectionC2S::receive);
        registerC2S(StaffTakeLensC2S.ID, StaffTakeLensC2S.CODEC, StaffTakeLensC2S::receive);
    }

    public static void registerClientSide() {
        // animation
        registerHandlerS2C(StartBlockAnimS2C.ID, StartBlockAnimS2C::receive);
        registerHandlerS2C(Stop2BlockAnimS2C.ID, Stop2BlockAnimS2C::receive);
        registerHandlerS2C(StopBlockAnimS2C.ID, StopBlockAnimS2C::receive);
        registerHandlerS2C(SwitchBlockAnimS2C.ID, SwitchBlockAnimS2C::receive);

        // interaction
        registerHandlerS2C(RedstoneLensStreamS2C.ID, RedstoneLensStreamS2C::receive);
        registerHandlerS2C(RemoveBlockEntityS2C.ID, RemoveBlockEntityS2C::receive);
    }

    private static <T extends AbstractC2SPacket> void registerC2S(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
    }

    private static <T extends AbstractS2CPacket> void registerS2C(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(id, codec);
    }

    private static <T extends AbstractS2CPacket> void registerHandlerS2C(CustomPayload.Id<T> id, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
    }
}

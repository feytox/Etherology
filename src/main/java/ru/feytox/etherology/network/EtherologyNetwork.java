package ru.feytox.etherology.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.StopBlockAnimS2C;
import ru.feytox.etherology.network.animation.SwitchBlockAnimS2C;
import ru.feytox.etherology.network.interaction.*;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

public class EtherologyNetwork {

    // TODO: 08.12.2024 move C2S

    public static void registerCommonSide() {
        // animation
        registerS2C(StartBlockAnimS2C.ID, StartBlockAnimS2C.CODEC);
        registerS2C(StopBlockAnimS2C.ID, StopBlockAnimS2C.CODEC);
        registerS2C(SwitchBlockAnimS2C.ID, SwitchBlockAnimS2C.CODEC);

        // interaction
        registerS2C(RedstoneLensStreamS2C.ID, RedstoneLensStreamS2C.CODEC);
        registerS2C(RemoveBlockEntityS2C.ID, RemoveBlockEntityS2C.CODEC);
        registerC2S(StaffMenuSelectionC2S.ID, StaffMenuSelectionC2S.CODEC, StaffMenuSelectionC2S::receive);
        registerC2S(StaffTakeLensC2S.ID, StaffTakeLensC2S.CODEC, StaffTakeLensC2S::receive);
        registerC2S(QuestCompleteC2S.ID, QuestCompleteC2S.CODEC, QuestCompleteC2S::receive);

        // entity components
        registerTypedC2S(EntityComponentC2SType.TELDECORE_SELECTED);
        registerTypedC2S(EntityComponentC2SType.TELDECORE_PAGE);
        registerTypedC2S(EntityComponentC2SType.TELDECORE_TAB);
        registerTypedC2S(EntityComponentC2SType.TELDECORE_OPENED);
    }

    private static <T extends AbstractC2SPacket> void registerTypedC2S(AbstractC2SPacket.PacketType<T> packetType) {
        registerC2S(packetType.getId(), packetType.getCodec(), packetType.getHandler());
    }

    private static <T extends AbstractC2SPacket> void registerC2S(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
    }

    private static <T extends AbstractS2CPacket> void registerS2C(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(id, codec);
    }
}

package ru.feytox.etherology.network.interaction;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.util.misc.EIdentifier;

public record StaffTakeLensC2S(ItemStack staffStack) implements AbstractC2SPacket {

    public static final Id<StaffTakeLensC2S> ID = new Id<>(EIdentifier.of("staff_take_lens_c2s"));
    public static final PacketCodec<RegistryByteBuf, StaffTakeLensC2S> CODEC = PacketCodec.tuple(ItemStack.OPTIONAL_PACKET_CODEC, StaffTakeLensC2S::staffStack, StaffTakeLensC2S::new);

    public static void receive(StaffTakeLensC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();

        context.server().execute(() -> {
            ItemStack staffStack = StaffMenuSelectionC2S.findInHands(player, packet.staffStack);
            if (staffStack == null) return;

            ItemStack lensStack = LensItem.takeLensFromStaff(staffStack);
            if (lensStack != null) player.giveItemStack(lensStack);
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

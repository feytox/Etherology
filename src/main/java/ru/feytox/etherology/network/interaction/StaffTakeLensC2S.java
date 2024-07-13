package ru.feytox.etherology.network.interaction;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.util.misc.CodecUtil;
import ru.feytox.etherology.util.misc.EIdentifier;

public record StaffTakeLensC2S() implements AbstractC2SPacket {

    public static final Id<StaffTakeLensC2S> ID = new Id<>(EIdentifier.of("staff_take_lens_c2s"));
    public static final PacketCodec<RegistryByteBuf, StaffTakeLensC2S> CODEC = CodecUtil.unitUnchecked(new StaffTakeLensC2S());

    public static void receive(StaffTakeLensC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();

        context.server().execute(() -> {
            ItemStack staffStack = StaffItem.getStaffStackFromHand(player);
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

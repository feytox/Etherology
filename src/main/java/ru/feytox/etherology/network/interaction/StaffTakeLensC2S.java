package ru.feytox.etherology.network.interaction;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.C2SPacketInfo;
import ru.feytox.etherology.util.misc.EIdentifier;

public class StaffTakeLensC2S extends AbstractC2SPacket {

    public static final Identifier ID = EIdentifier.of("staff_take_lens_c2s");

    public static void receive(C2SPacketInfo packetInfo) {
        ServerPlayerEntity player = packetInfo.player();
        MinecraftServer server = packetInfo.server();

        server.execute(() -> {
            ItemStack staffStack = StaffItem.getStaffStackFromHand(player);
            if (staffStack == null) return;

            ItemStack lensStack = LensItem.takeLensFromStaff(staffStack);
            if (lensStack != null) player.giveItemStack(lensStack);
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return ID;
    }
}

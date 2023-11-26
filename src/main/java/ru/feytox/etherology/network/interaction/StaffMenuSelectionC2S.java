package ru.feytox.etherology.network.interaction;

import lombok.RequiredArgsConstructor;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.staff.LensSelectionType;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.C2SPacketInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@RequiredArgsConstructor
public class StaffMenuSelectionC2S extends AbstractC2SPacket {

    public static final Identifier ID = new EIdentifier("staff_place_lens_c2s");

    private final LensSelectionType selected;
    private final ItemStack lensStack;

    public static void receive(C2SPacketInfo packetInfo) {
        LensSelectionType selected = packetInfo.buf().readEnumConstant(LensSelectionType.class);
        if (selected.equals(LensSelectionType.NONE)) return;

        ItemStack selectedStack = packetInfo.buf().readItemStack();
        ServerPlayerEntity player = packetInfo.player();
        MinecraftServer server = packetInfo.server();

        server.execute(() -> {
            ItemStack staffStack = StaffItem.getStaffStackFromHand(player);
            if (staffStack == null) return;

            ItemStack stack = selectedStack;
            if (!selected.isEmptySelectedItem()) {
                ItemStack foundStack = findOriginal(player.getInventory(), stack);
                if (foundStack == null) return;
                stack = foundStack;
            }

            selected.getHandler().handle(player, staffStack, stack);
        });
    }

    @Nullable
    private static ItemStack findOriginal(Inventory inventory, ItemStack copyStack) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && ItemStack.canCombine(stack, copyStack)) return stack;
        }

        return null;
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeEnumConstant(selected);
        buf.writeItemStack(lensStack);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return ID;
    }
}

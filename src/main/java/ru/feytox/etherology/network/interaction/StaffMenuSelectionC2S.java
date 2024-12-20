package ru.feytox.etherology.network.interaction;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.staff.LensSelectionType;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.util.misc.EIdentifier;

public record StaffMenuSelectionC2S(LensSelectionType selected, ItemStack staffStack, ItemStack lensStack) implements AbstractC2SPacket {

    public static final Id<StaffMenuSelectionC2S> ID = new Id<>(EIdentifier.of("staff_menu_c2s"));
    public static final PacketCodec<RegistryByteBuf, StaffMenuSelectionC2S> CODEC = PacketCodec.tuple(LensSelectionType.PACKET_CODEC, StaffMenuSelectionC2S::selected, ItemStack.OPTIONAL_PACKET_CODEC, StaffMenuSelectionC2S::staffStack, ItemStack.OPTIONAL_PACKET_CODEC, StaffMenuSelectionC2S::lensStack, StaffMenuSelectionC2S::new);

    public static void receive(StaffMenuSelectionC2S packet, ServerPlayNetworking.Context context) {
        if (packet.selected.equals(LensSelectionType.NONE)) return;
        ServerPlayerEntity player = context.player();

        context.server().execute(() -> {
            ItemStack staffStack = findInHands(player, packet.staffStack);
            if (staffStack == null) return;

            ItemStack stack = packet.lensStack;
            if (!packet.selected.isEmptySelectedItem()) {
                ItemStack foundStack = findOriginal(player.getInventory(), stack);
                if (foundStack == null) return;
                stack = foundStack;
            }

            packet.selected.getHandler().handle(player, staffStack, stack);
        });
    }

    // TODO: 15.07.2024 use different methods of validating item stacks.
    @Nullable
    private static ItemStack findOriginal(Inventory inventory, ItemStack copyStack) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, copyStack)) return stack;
        }

        return null;
    }

    @Nullable
    public static ItemStack findInHands(LivingEntity entity, ItemStack copyStack) {
        for (ItemStack stack : entity.getHandItems()) {
            if (ItemStack.areItemsAndComponentsEqual(stack, copyStack)) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

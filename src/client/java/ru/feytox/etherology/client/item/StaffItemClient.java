package ru.feytox.etherology.client.item;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.client.gui.staff.StaffLensesScreen;
import ru.feytox.etherology.client.registry.KeybindsRegistry;

import static ru.feytox.etherology.item.StaffItem.getStaffInHands;

@UtilityClass
public class StaffItemClient {

    public static void tickStaff(ItemStack stack, PlayerEntity player) {
        var client = MinecraftClient.getInstance();

        ItemStack selectedStack = getStaffInHands(player);
        if (stack == null) {
            if (client.currentScreen instanceof StaffLensesScreen screen) screen.tryClose();
            return;
        }
        if (!stack.equals(selectedStack)) return;

        tickLensesMenu(client);
    }

    public static void tickLensesMenu(@NonNull MinecraftClient client) {
        boolean isPressed = KeybindsRegistry.isPressed(KeybindsRegistry.STAFF_INTERACTION);
        boolean isSneakPressed = KeybindsRegistry.isPressed(client.options.sneakKey);

        if (!(client.currentScreen instanceof StaffLensesScreen screen)) {
            if (isSneakPressed) return;
            if (isPressed && client.currentScreen == null) {
                client.setScreen(new StaffLensesScreen(null));
            }
            return;
        }

        if (!isPressed) {
            screen.tryClose();
            return;
        }

        screen.tryOpen();
    }
}

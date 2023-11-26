package ru.feytox.etherology.gui.staff;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LenseItem;
import ru.feytox.etherology.magic.staff.LenseData;

@RequiredArgsConstructor
public enum LensSelectionType {
    NONE(true, (p, s, l) -> {}),
    UP_ARROW(true, arrowHandler(LenseData.LenseMode.UP)),
    DOWN_ARROW(true, arrowHandler(LenseData.LenseMode.DOWN)),
    ITEM(false, itemHandler());

    @Getter
    private final boolean emptySelectedItem;

    @Getter @NonNull
    private final SelectionHandler handler;

    private static SelectionHandler arrowHandler(LenseData.LenseMode lenseMode) {
        return (player, staffStack, lensStack) -> {
            LenseData lenseData = LenseItem.readNbt(staffStack.getNbt());
            if (lenseData == null) throw new NullPointerException("Failed to read lense data from staff");
            lenseData.setLenseMode(lenseMode);
            LenseItem.writeNbt(staffStack.getNbt(), lenseData);
        };
    }

    private static SelectionHandler itemHandler() {
        return (player, staffStack, lensStack) -> {
            if (lensStack == null || lensStack.isEmpty()) throw new NullPointerException("Null or empty lens stack provided for item selection handler");
            ItemStack prevLens = LenseItem.takeLenseFromStaff(staffStack);
            if (prevLens != null) player.giveItemStack(prevLens);

            LenseItem.placeLenseOnStaff(staffStack, lensStack);
        };
    }

    @FunctionalInterface
    public interface SelectionHandler {

        void handle(ServerPlayerEntity player, ItemStack staffStack, @Nullable ItemStack lensStack);
    }
}

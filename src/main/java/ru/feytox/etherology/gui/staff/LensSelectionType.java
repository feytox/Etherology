package ru.feytox.etherology.gui.staff;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lens.LensMode;
import ru.feytox.etherology.registry.util.EtherologyComponents;

@Getter
@RequiredArgsConstructor
public enum LensSelectionType {
    NONE(true, (p, s, l) -> {}),
    UP_ARROW(true, arrowHandler(LensMode.STREAM)),
    DOWN_ARROW(true, arrowHandler(LensMode.CHARGE)),
    ITEM(false, itemHandler());

    private final boolean emptySelectedItem;

    @NonNull
    private final SelectionHandler handler;

    private static SelectionHandler arrowHandler(LensMode lensMode) {
        return (player, staffStack, lensStack) -> {
            val lens = EtherologyComponents.LENS.get(staffStack);
            lens.setLensMode(lensMode);
        };
    }

    private static SelectionHandler itemHandler() {
        return (player, staffStack, lensStack) -> {
            if (lensStack == null || lensStack.isEmpty()) throw new NullPointerException("Null or empty lens stack provided for item selection handler");
            ItemStack prevLens = LensItem.takeLenseFromStaff(staffStack);
            if (prevLens != null) player.giveItemStack(prevLens);

            LensItem.placeLenseOnStaff(staffStack, lensStack);
        };
    }

    @FunctionalInterface
    public interface SelectionHandler {

        void handle(ServerPlayerEntity player, ItemStack staffStack, @Nullable ItemStack lensStack);
    }
}

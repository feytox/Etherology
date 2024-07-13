package ru.feytox.etherology.gui.staff;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensMode;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.util.misc.CodecUtil;

@Getter
@RequiredArgsConstructor
public enum LensSelectionType {
    NONE(true, (p, s, l) -> {}),
    UP_ARROW(true, arrowHandler(LensMode.STREAM)),
    DOWN_ARROW(true, arrowHandler(LensMode.CHARGE)),
    ITEM(false, itemHandler());

    public static final PacketCodec<ByteBuf, LensSelectionType> PACKET_CODEC = CodecUtil.ofEnum(values());

    private final boolean emptySelectedItem;

    @NonNull
    private final SelectionHandler handler;

    private static SelectionHandler arrowHandler(LensMode lensMode) {
        return (player, staffStack, lensStack) -> {
            ItemStack staffLensStack = LensItem.getStaffLens(staffStack);
            if (staffLensStack == null) return;
            staffLensStack = staffLensStack.copy();
            LensComponent.getWrapper(staffLensStack).ifPresent(lens -> lens.set(lensMode, LensComponent::withMode).save());
            staffStack.set(ComponentTypes.STAFF_LENS, staffLensStack);
        };
    }

    private static SelectionHandler itemHandler() {
        return (player, staffStack, lensStack) -> {
            if (lensStack == null || lensStack.isEmpty()) throw new NullPointerException("Null or empty lens stack provided for item selection handler");
            ItemStack prevLens = LensItem.takeLensFromStaff(staffStack);
            if (prevLens != null) player.giveItemStack(prevLens);

            LensItem.placeLensOnStaff(staffStack, lensStack);
        };
    }

    @FunctionalInterface
    public interface SelectionHandler {

        void handle(ServerPlayerEntity player, ItemStack staffStack, @Nullable ItemStack lensStack);
    }
}

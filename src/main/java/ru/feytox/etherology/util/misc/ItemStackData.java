package ru.feytox.etherology.util.misc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.item.ItemStack;

// non-persistent data for ItemStack
@NoArgsConstructor @Getter @Setter
public class ItemStackData {

    private long lastWarpTick = 0;
    private Float lastWarpLevel = null;
    private Float targetWarpLevel = null;

    public static ItemStackData get(ItemStack stack) {
        return ((ItemStackDataProvider)(Object) stack).etherology$getStackData();
    }
}

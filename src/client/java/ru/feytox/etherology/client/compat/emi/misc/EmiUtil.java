package ru.feytox.etherology.client.compat.emi.misc;

import dev.emi.emi.api.stack.EmiStack;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemConvertible;

@UtilityClass
public class EmiUtil {

    public static EmiStack ofEmpty(ItemConvertible item, long amount) {
        if (amount == 0) return EmiStack.EMPTY;
        return EmiStack.of(item, amount);
    }
}

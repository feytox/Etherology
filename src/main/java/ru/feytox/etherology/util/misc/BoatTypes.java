package ru.feytox.etherology.util.misc;

import com.google.common.base.Suppliers;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import ru.feytox.etherology.registry.item.EItems;

import java.util.function.Supplier;

@UtilityClass
public class BoatTypes {

    public static final Supplier<BoatEntity.Type> PEACH_TYPE = Suppliers.memoize(() -> BoatEntity.Type.valueOf("ETHEROLOGY_PEACH"));

    public static Item getBoatFromType(Item original, BoatEntity.Type type, boolean chest) {
        if (type.equals(PEACH_TYPE.get())) return chest ? EItems.PEACH_CHEST_BOAT : EItems.PEACH_BOAT;
        return original;
    }
}

package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.BoatDispenserBehavior;
import ru.feytox.etherology.block.etherealGenerators.EtherealGeneratorDispenserBehavior;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.misc.BoatTypes;

@UtilityClass
public class DispenserBehaviors {

    public static void registerAll() {
        DispenserBlock.registerBehavior(EItems.THUJA_OIL, new EtherealGeneratorDispenserBehavior());
        DispenserBlock.registerBehavior(EItems.PEACH_BOAT, new BoatDispenserBehavior(BoatTypes.PEACH_TYPE.get()));
        DispenserBlock.registerBehavior(EItems.PEACH_CHEST_BOAT, new BoatDispenserBehavior(BoatTypes.PEACH_TYPE.get(), true));
        // PedestalDispenserBehavior is registering in DispenserBlockMixin
    }
}

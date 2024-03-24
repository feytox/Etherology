package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.minecraft.block.DispenserBlock;
import ru.feytox.etherology.block.etherealGenerators.EtherealGeneratorDispenserBehavior;
import ru.feytox.etherology.registry.item.EItems;

@UtilityClass
public class DispenserBehaviors {

    public static void registerAll() {
        DispenserBlock.registerBehavior(EItems.THUJA_OIL, new EtherealGeneratorDispenserBehavior());
        // PedestalDispenserBehavior is registering in DispenserBlockMixin
    }
}

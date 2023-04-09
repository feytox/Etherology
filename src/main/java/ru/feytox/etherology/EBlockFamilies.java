package ru.feytox.etherology;

import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;

import static ru.feytox.etherology.DecoBlocks.*;

public class EBlockFamilies {
    public static final BlockFamily PEACH;

    public static void registerFamilies() {}

    static {
        PEACH = BlockFamilies.register(PEACH_PLANKS).button(PEACH_BUTTON).door(PEACH_DOOR).fence(PEACH_FENCE).fenceGate(PEACH_FENCE_GATE).pressurePlate(PEACH_PRESSURE_PLATE).sign(PEACH_SIGN, PEACH_WALL_SIGN).slab(PEACH_SLAB).stairs(PEACH_STAIRS).trapdoor(PEACH_TRAPDOOR).group("wooden").unlockCriterionName("has_planks").build();
    }
}

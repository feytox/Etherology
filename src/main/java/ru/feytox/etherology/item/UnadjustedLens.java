package ru.feytox.etherology.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.lense.LensComponent;

public class UnadjustedLens extends LensItem {

    @Override
    public boolean onStreamUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold) {
        return false;
    }

    @Override
    public boolean onChargeUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold) {
        return false;
    }

    @Override
    public boolean isAdjusted() {
        return false;
    }
}

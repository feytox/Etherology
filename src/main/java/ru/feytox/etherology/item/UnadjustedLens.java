package ru.feytox.etherology.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.util.misc.ItemData;

import java.util.function.Supplier;

public class UnadjustedLens extends LensItem {

    public UnadjustedLens() {
        super(null, 0.0f, 0.0f);
    }

    @Override
    public boolean onStreamUse(World world, LivingEntity entity, ItemData<LensComponent> lensData, ItemStack lensStack, boolean hold, Supplier<Hand> handGetter) {
        return false;
    }

    @Override
    public boolean onChargeUse(World world, LivingEntity entity, ItemData<LensComponent> lensData, ItemStack lensStack, boolean hold, Supplier<Hand> handGetter) {
        return false;
    }

    @Override
    public boolean isUnadjusted() {
        return true;
    }
}

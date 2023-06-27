package ru.feytox.etherology.block.crucible;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import static ru.feytox.etherology.registry.block.EBlocks.CRUCIBLE_BLOCK_ENTITY;

public class CrucibleBlockEntity extends TickableBlockEntity implements EGeoBlockEntity, ImplementedInventory {
    private static final RawAnimation MIXING_ANIM = RawAnimation.begin()
            .thenPlay("animation.brewingCauldron.mixing");
    private static final RawAnimation FAILED_ANIM = RawAnimation.begin()
            .thenPlay("animation.brewingCauldron.failed");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin()
            .thenPlay("animation.brewingCauldron.idle");
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(32, ItemStack.EMPTY);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(CRUCIBLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
       controllers.add(getTriggerController("mixing", MIXING_ANIM, 1));
       controllers.add(getTriggerController("failed", FAILED_ANIM));
       controllers.add(getTriggerController("idle", IDLE_ANIM));
    }

    @Override
    public double getBoneResetTime() {
        return 0.0001;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

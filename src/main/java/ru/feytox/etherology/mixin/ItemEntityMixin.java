package ru.feytox.etherology.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.ItemsRegistry;
import ru.feytox.etherology.items.CarriedCrateItem;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void onSetStack(ItemStack stack, CallbackInfo ci) {
        if (stack.isOf(ItemsRegistry.CARRIED_CRATE)) {
            ItemEntity it = ((ItemEntity)(Object) this);
            CarriedCrateItem.placeFallingCrate(it.world, it.getBlockPos(), stack, Direction.NORTH, null);
            it.kill();
            ci.cancel();
        }
    }
}

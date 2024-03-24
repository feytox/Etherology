package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.block.pedestal.PedestalDispenserBehavior;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @ModifyExpressionValue(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DispenserBlock;getBehaviorForItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;"))
    private DispenserBehavior injectPedestalBehavior(DispenserBehavior original, @Local BlockPointerImpl pointer, @Local ItemStack stack) {
        return PedestalDispenserBehavior.testDispenser(pointer, stack) ? PedestalDispenserBehavior.getInstance() : original;
    }
}

package ru.feytox.etherology.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.item.TwoHandheldSword;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void banExtraItemsUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (TwoHandheldSword.hideExtraItem(user, stack)) cir.setReturnValue(TypedActionResult.pass(stack));
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void banExtraItemsUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = context.getStack();
        if (TwoHandheldSword.hideExtraItem(context.getPlayer(), stack)) cir.setReturnValue(ActionResult.PASS);
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void banExtraItemsUseOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (TwoHandheldSword.hideExtraItem(user, stack)) cir.setReturnValue(ActionResult.PASS);
    }
}

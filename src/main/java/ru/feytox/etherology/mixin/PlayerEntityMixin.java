package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.animation.PlayerAnimationController;
import ru.feytox.etherology.item.GlaiveItem;
import ru.feytox.etherology.registry.item.ToolItems;

import java.util.concurrent.CompletableFuture;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    private void onPlayerTick(CallbackInfo ci) {
        PlayerEntity it = ((PlayerEntity) (Object) this);
        if (!(it instanceof AbstractClientPlayerEntity clientPlayer)) return;
        CompletableFuture.runAsync(() -> PlayerAnimationController.tickAnimations(clientPlayer));
    }

    @Inject(method = "takeShieldHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;disablesShield()Z"), cancellable = true)
    private void onTakeIronShieldHit(LivingEntity attacker, CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (!player.getActiveItem().isOf(ToolItems.IRON_SHIELD)) return;
        if (attacker.getMainHandStack().getItem() instanceof AxeItem) ci.cancel();
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box glaiveSweepExpand(Box instance, double x, double y, double z, Operation<Box> original) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!GlaiveItem.checkGlaive(attacker)) return original.call(instance, x, y, z);
        return original.call(instance, 4.0, 1.0, 4.0);
    }
}

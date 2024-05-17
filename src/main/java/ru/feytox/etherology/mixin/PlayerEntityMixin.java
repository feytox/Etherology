package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.animation.PlayerAnimationController;
import ru.feytox.etherology.item.BroadSwordItem;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.misc.PlayerSessionData;
import ru.feytox.etherology.util.misc.PlayerSessionDataProvider;
import ru.feytox.etherology.util.misc.ShockwaveUtil;

import java.util.concurrent.CompletableFuture;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerSessionDataProvider {

    @Unique
    private final PlayerSessionData etherology$playerSessionData = new PlayerSessionData();

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
    private Box broadSwordSweepExpand(Box instance, double x, double y, double z, Operation<Box> original) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!BroadSwordItem.isUsing(attacker)) return original.call(instance, x, y, z);
        return original.call(instance, 4.0, 1.0, 4.0);
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void injectPealEffect(Entity target, CallbackInfo ci) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (ShockwaveUtil.onAttack(attacker, target)) ci.cancel();
    }

    @WrapWithCondition(method = "spawnSweepAttackParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private <T extends ParticleEffect> boolean replaceBroadSwordSweepParticles(ServerWorld instance, T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (!BroadSwordItem.isUsing(player)) return true;
        BroadSwordItem.replaceSweepParticle(instance, x, y, z);
        return false;
    }

    @Override
    public PlayerSessionData etherology$getData() {
        return etherology$playerSessionData;
    }
}

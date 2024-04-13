package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.animation.PlayerAnimationController;
import ru.feytox.etherology.item.GlaiveItem;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.misc.ShockwaveUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"))
    private float getHammerEnchantDamage(float original) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        return !(player.getMainHandStack().getItem() instanceof HammerItem) || player.getOffHandStack().isEmpty() ? original : 0;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    private double getHammerDamage(double original) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        return !(player.getMainHandStack().getItem() instanceof HammerItem) || player.getOffHandStack().isEmpty() ? original : 0;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onPlayerTick(CallbackInfo ci) {
        PlayerEntity it = ((PlayerEntity) (Object) this);
        if (!(it instanceof AbstractClientPlayerEntity clientPlayer)) return;
        CompletableFuture.runAsync(() -> PlayerAnimationController.tickAnimations(clientPlayer));
    }

    @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;spawnSweepAttackParticles()V"))
    private boolean replaceSweepToHammer(PlayerEntity instance) {
        return !HammerItem.checkHammer(instance);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void replaceSoundToHammer(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!HammerItem.checkHammer(attacker)) {
            original.call(instance, except, x, y, z, sound, category, volume, pitch);
            return;
        }

        if (!sound.equals(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE)) {
            float pitchVal = 0.9f + instance.random.nextFloat() * 0.2f;
            Executor delayedExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS);
            CompletableFuture.runAsync(() ->
                    instance.playSound(except, x, y, z, EtherSounds.HAMMER_DAMAGE, category, 0.5f, pitchVal), delayedExecutor);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void replaceHammerAttack(Entity target, CallbackInfo ci) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        float cooldown = attacker.getAttackCooldownProgress(0.0f);
        if (cooldown < 0.9) return;
        if (!ShockwaveUtil.onFullAttack(attacker)) return;

        attacker.resetLastAttackedTicks();
        ci.cancel();
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

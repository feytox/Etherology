package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.BroadSwordItem;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.misc.ShockwaveUtil;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

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

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void replaceBroadSwordSound(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        SoundEvent newSound = BroadSwordItem.replaceAttackSound(player, sound);
        original.call(instance, except, x, y, z, newSound, category, volume, pitch);
    }

    @Inject(method = "damageArmor", at = @At("HEAD"))
    private void damageArmorByPick(DamageSource source, float amount, CallbackInfo ci, @Local(argsOnly = true) LocalFloatRef amountRef) {
        if (!(source.getAttacker() instanceof LivingEntity entity)) return;
        if (!(entity.getMainHandStack().getItem() instanceof BattlePickaxe pick)) return;

        amountRef.set(Math.round(amount * (1.5 + pick.getDamagePercent())));
    }
}

package ru.feytox.etherology.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.particle.ShockwaveParticle;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.feyapi.PlayerAnimations;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"))
    private float hammerEnchantDamage(ItemStack stack, EntityGroup group) {
        float damage = EnchantmentHelper.getAttackDamage(stack, group);
        if (!(stack.getItem() instanceof HammerItem)) return damage;
        PlayerEntity player = ((PlayerEntity) (Object) this);

        return player.getOffHandStack().isEmpty() ? damage : 0;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    private double hammerDamage(PlayerEntity player, EntityAttribute entityAttribute) {
        double attribute = player.getAttributeValue(entityAttribute);
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof HammerItem) || player.getOffHandStack().isEmpty()) return attribute;
        return 0;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onPlayerTick(CallbackInfo ci) {
        PlayerEntity it = ((PlayerEntity) (Object) this);
        if (!(it instanceof AbstractClientPlayerEntity clientPlayer)) return;
        CompletableFuture.runAsync(() -> PlayerAnimations.tickAnimations(clientPlayer));
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 1))
    private void vanillaKnockbackCancel(LivingEntity instance, double strength, double x, double z) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!HammerItem.checkHammer(attacker)) {
            instance.takeKnockback(strength, x, z);
        }
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 0))
    private void shockwaveKnockback(LivingEntity instance, double strength, double x, double z) {
        instance.takeKnockback(strength, x, z);
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!HammerItem.checkHammer(attacker)) return;
        World world = attacker.getWorld();
        if (world.isClient) return;

        ShockwaveParticle.spawnParticle((ServerWorld) world, instance);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onHammerKnockback(Entity target, CallbackInfo ci, float f, float g, boolean bl, boolean bl2, int i, boolean bl3, boolean bl4, float j, boolean bl5, int k, Vec3d vec3d, float l, List<LivingEntity> list, Iterator<LivingEntity> var19, LivingEntity livingEntity) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!HammerItem.checkHammer(attacker)) return;
        Vec3d hammerVec = livingEntity.getPos().subtract(target.getPos());
        float radius = 2f * l;
        float distance = livingEntity.distanceTo(target);
        if (distance > radius) return;
        hammerVec = hammerVec.multiply(2 * radius / distance);
        livingEntity.takeKnockback(1, hammerVec.x, hammerVec.z);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;squaredDistanceTo(Lnet/minecraft/entity/Entity;)D"))
    private double hammerWaveDistance(PlayerEntity instance, Entity entity) {
        double distance = instance.squaredDistanceTo(entity);
        return HammerItem.checkHammer(instance) ? 0 : distance;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box expandHammerAttack(Box instance, double x, double y, double z) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!HammerItem.checkHammer(attacker)) {
            return instance.expand(x, y, z);
        }

        return instance.expand(x+4, y, z+4);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void onPlaySound(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!HammerItem.checkHammer(attacker)) {
            instance.playSound(except, x, y, z, sound, category, volume, pitch);
            return;
        }

        if (!sound.equals(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE)) {
            float pitchVal = 0.9f + instance.random.nextFloat() * 0.2f;
            Executor delayedExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS);
            CompletableFuture.runAsync(() ->
                    instance.playSound(except, x, y, z, EtherSounds.HAMMER_DAMAGE, category, 0.5f, pitchVal), delayedExecutor);
        }
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;spawnSweepAttackParticles()V"))
    private void replaceSweepToHammer(PlayerEntity instance) {
        if (!HammerItem.checkHammer(instance)) {
            instance.spawnSweepAttackParticles();
        }
    }
}

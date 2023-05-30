package ru.feytox.etherology.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.animation.PlayerAnimationController;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.feyapi.ShockwaveUtil;

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
        CompletableFuture.runAsync(() -> PlayerAnimationController.tickAnimations(clientPlayer));
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;spawnSweepAttackParticles()V"))
    private void replaceSweepToHammer(PlayerEntity instance) {
        if (!HammerItem.checkHammer(instance)) {
            instance.spawnSweepAttackParticles();
        }
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

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void replaceHammerAttack(Entity target, CallbackInfo ci) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        float cooldown = attacker.getAttackCooldownProgress(0.0f);
        if (cooldown < 0.9) return;
        if (!ShockwaveUtil.onFullAttack(attacker)) return;

        attacker.resetLastAttackedTicks();
        ci.cancel();
    }
}

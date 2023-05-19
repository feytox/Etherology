package ru.feytox.etherology.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.util.feyapi.PlayerAnimations;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"))
    private void vanillaKnockbackCancel(LivingEntity instance, double strength, double x, double z) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (attacker.getMainHandStack().getItem() instanceof HammerItem && attacker.getOffHandStack().isEmpty()) return;
        instance.takeKnockback(strength, x, z);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onHammerKnockback(Entity target, CallbackInfo ci, float f, float g, boolean bl, boolean bl2, int i, boolean bl3, boolean bl4, float j, boolean bl5, int k, Vec3d vec3d, float l, List<LivingEntity> list, Iterator<LivingEntity> var19, LivingEntity livingEntity) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!(attacker.getMainHandStack().getItem() instanceof HammerItem hammer) || !attacker.getOffHandStack().isEmpty()) return;
        Vec3d hammerVec = livingEntity.getPos().subtract(target.getPos());
        float radius = 2f * hammer.getAttackDamage() * l;
        float distance = livingEntity.distanceTo(target);
        if (distance > radius) return;
        hammerVec = hammerVec.multiply(2 * radius / distance);
        livingEntity.takeKnockback(1, hammerVec.x, hammerVec.z);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box expandHammerAttack(Box instance, double x, double y, double z) {
        PlayerEntity attacker = ((PlayerEntity) (Object) this);
        if (!(attacker.getMainHandStack().getItem() instanceof HammerItem) || !attacker.getOffHandStack().isEmpty()) {
            return instance.expand(x, y, z);
        }

        return instance.expand(x+4, y, z+4);
    }
}

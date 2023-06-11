package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.enchantment.ReflectionEnchantment;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.EtherShield;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.registry.util.EtherSounds;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "applyArmorToDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F"), cancellable = true)
    private void getDamageByPick(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (!(source instanceof EntityDamageSource entitySource)) return;
        if (!(entitySource.getAttacker() instanceof LivingEntity entity)) return;
        if (!(entity.getMainHandStack().getItem() instanceof BattlePickaxe pick)) return;

        LivingEntity it = ((LivingEntity) (Object) this);
        amount = DamageUtil.getDamageLeft(amount, it.getArmor(), (float) it.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        amount = Math.min(amount + 0.5f * pick.getDamagePercent() * it.getArmor(), amount * 1.5f);
        cir.setReturnValue(amount);
    }

    @Redirect(method = "modifyAppliedDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Ljava/lang/Iterable;Lnet/minecraft/entity/damage/DamageSource;)I"))
    private int getProtectionOnPick(Iterable<ItemStack> equipment, DamageSource source) {
        int i = EnchantmentHelper.getProtectionAmount(equipment, source);
        if (!(source instanceof EntityDamageSource entitySource)) return i;
        if (!(entitySource.getAttacker() instanceof LivingEntity entity)) return i;
        if (!(entity.getMainHandStack().getItem() instanceof BattlePickaxe pick)) return i;

        float k = pick.getDamagePercent();
        return Math.round(i * (1 - 0.5f * k));
    }

    @Inject(method = "blockedByShield", at = @At(value = "HEAD"), cancellable = true)
    private void onShieldBlocking(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity shieldHolder = ((LivingEntity) (Object) this);
        if (!(shieldHolder.getActiveItem().getItem() instanceof EtherShield)) return;

        cir.setReturnValue(modifiedBlockedByShield(shieldHolder, source));
    }

    private static boolean modifiedBlockedByShield(LivingEntity shieldHolder, DamageSource source) {
        Entity entity = source.getSource();

        boolean isProjectile = entity instanceof ProjectileEntity;
        boolean isPersistentProjectile = false;
        if (isProjectile && entity instanceof PersistentProjectileEntity persistentProjectile) {
            isPersistentProjectile = true;
            if (persistentProjectile.getPierceLevel() > 0) {
                return false;
            }
        }

        if (!source.bypassesArmor() && shieldHolder.isBlocking()) {
            Vec3d vec3d = source.getPosition();
            if (vec3d != null) {
                Vec3d vec3d2 = shieldHolder.getRotationVec(1.0F);
                Vec3d vec3d3 = vec3d.relativize(shieldHolder.getPos()).normalize();
                vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);

                // 90 -> 30 degrees
                double cosVal = isProjectile ? 0.0 : -0.866025;
                boolean result = vec3d3.dotProduct(vec3d2) < cosVal;
                if (result && isProjectile) {
                    int reflectionLevel = EnchantmentHelper.getEquipmentLevel(ReflectionEnchantment.INSTANCE.get(), shieldHolder);
                    if (reflectionLevel > 0) {
                        Vec3d newVelocity = entity.getVelocity().multiply(8.5);
                        if (!isPersistentProjectile) newVelocity = newVelocity.negate();
                        entity.setVelocity(newVelocity);
                        shieldHolder.getEntityWorld().playSound(null, shieldHolder.getBlockPos(), EtherSounds.DEFLECT, shieldHolder.getSoundCategory(), 0.5f, 1.0f);
                        return true;
                    }
                }
                return result;
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    @ModifyReturnValue(method = "disablesShield", at = @At("RETURN"))
    private boolean onDisablesShield(boolean original) {
        LivingEntity it = ((LivingEntity) (Object) this);
        if (!(it instanceof PlayerEntity player)) return original || it.getMainHandStack().getItem() instanceof HammerItem;
        return original || HammerItem.checkHammer(player);
    }
}

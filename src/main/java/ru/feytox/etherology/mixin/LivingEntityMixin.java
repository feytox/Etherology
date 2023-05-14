package ru.feytox.etherology.mixin;

import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;
import ru.feytox.etherology.util.feyapi.PlayerAnimations;

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

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void onSwingHand(Hand hand, CallbackInfo ci) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (!(entity instanceof AbstractClientPlayerEntity player)) return;
        if (!(player instanceof IAnimatedPlayer animatedPlayer)) return;
        if (!(player.getMainHandStack().getItem() instanceof HammerItem) || !player.getOffHandStack().isEmpty()) return;

        String animString = player.getMainArm().equals(Arm.LEFT) ? "right.twohanded.hit": "left.twohanded.hit";
        PlayerAnimations idle = player.getMainArm().equals(Arm.LEFT) ? PlayerAnimations.LEFT_TWOHANDED_IDLE : PlayerAnimations.RIGHT_TWOHANDED_IDLE;
        PlayerAnimations.setAnimation(animatedPlayer, false,
                new PlayerAnimations.AnimationData(new EIdentifier(animString), 0, Ease.LINEAR, true),
                null, idle);
    }
}

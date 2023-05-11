package ru.feytox.etherology.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.feytox.etherology.item.BattlePickaxe;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "damageArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void damageArmorByPick(DamageSource source, float amount, int[] slots, CallbackInfo ci, int[] var4, int var5, int var6, int i, ItemStack itemStack) {
        if (!(source instanceof EntityDamageSource entitySource) || source.bypassesArmor()) return;
        if (!(entitySource.getAttacker() instanceof LivingEntity entity)) return;
        if (!(entity.getMainHandStack().getItem() instanceof BattlePickaxe pick)) return;

        amount = Math.round(amount * (1.5 + pick.getDamagePercent()));
        itemStack.damage((int) amount, player, (plr) -> plr.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i)));
    }
}

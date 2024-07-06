package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.enchantment.ReflectionEnchantment;
import ru.feytox.etherology.item.EtherShield;
import ru.feytox.etherology.registry.misc.EtherSounds;

import java.util.Optional;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V"), cancellable = true)
    private void cancelByEtherShield(HitResult hitResult, CallbackInfo ci) {
        if (!(hitResult instanceof EntityHitResult entityHitResult)) return;
        ProjectileEntity projectile = ((ProjectileEntity) (Object) this);
        World world = projectile.method_48926();
        if (world == null || world.isClient) return;
        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof LivingEntity target)) return;
        Optional<ItemStack> optionalShield = EtherShield.getUsingShield(target);
        if (optionalShield.isEmpty()) return;
        ItemStack shield = optionalShield.get();
        if (EnchantmentHelper.getLevel(ReflectionEnchantment.INSTANCE.get(), shield) < 1) return;

        Vec3d targetRotation = target.getRotationVec(1.0F);
        Vec3d targetPos = target.getPos();
        if (!EtherShield.shieldBlockCheck(targetRotation, targetPos, projectile.getPos(), true)) return;

        Vec3d newVelocity = projectile.getVelocity().negate();
        Entity newProj = projectile.getType().create(world);
        if (newProj == null) return;

        newProj.copyFrom(projectile);
        newProj.refreshPositionAndAngles(projectile.getX(), projectile.getY(), projectile.getZ(), projectile.getYaw(), projectile.getPitch());
        newProj.setVelocity(newVelocity);
        projectile.setPosition(projectile.getPos().add(0, -10000, 0));
        ((EntityHitResultAccessor) entityHitResult).setEntity(projectile);
        projectile.discard();
        world.spawnEntity(newProj);

        world.playSound(null, target.getBlockPos(), EtherSounds.DEFLECT, target.getSoundCategory(), 0.5f, 1.0f);
        shield.damage(2, target, owner -> owner.sendToolBreakStatus(owner.getActiveHand()));
        ci.cancel();
    }
}

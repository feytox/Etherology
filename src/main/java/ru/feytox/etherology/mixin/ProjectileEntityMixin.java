package ru.feytox.etherology.mixin;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.registry.misc.EtherEnchantments;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V"), cancellable = true)
    private void cancelByIronShield(HitResult hitResult, CallbackInfo ci) {
        if (!(hitResult instanceof EntityHitResult entityHitResult)) return;
        ProjectileEntity projectile = ((ProjectileEntity) (Object) this);
        if (EtherEnchantments.applyReflection(entityHitResult, projectile)) return;
        ci.cancel();
    }
}

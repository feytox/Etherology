package ru.feytox.etherology.item;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class EtherShield extends FabricShieldItem {
    public EtherShield(Settings settings, int coolDownTicks, int enchantability, Item... repairItems) {
        super(settings, coolDownTicks, enchantability, repairItems);
    }

//    @Override
//    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
//        super.usageTick(world, user, stack, remainingUseTicks);
//        if (EnchantmentHelper.getLevel(ReflectionEnchantment.INSTANCE.get(), stack) == 0) return;
//        if (world.isClient) return;
//
//        Box projectileBox = user.getBoundingBox().expand(1.0, 0.5, 0.0);
//        List<ProjectileEntity> projectiles = world.getEntitiesByType(TypeFilter.instanceOf(ProjectileEntity.class), projectileBox, projectile -> true);
//        if (projectiles.isEmpty()) return;
//
//        Vec3d entityRotation = user.getRotationVec(1.0F);
//        Vec3d entityPos = user.getPos();
//        for (ProjectileEntity projectile : projectiles) {
//            if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
//                if (((PersistentProjectileEntityAccessor) persistentProjectile).isInGround()) continue;
//            }
//            if (!shieldBlockCheck(entityRotation, entityPos, projectile.getPos(), true)) continue;
//
//            Vec3d newVelocity = projectile.getVelocity().negate();
//            projectile.setVelocity(newVelocity);
//            world.playSound(null, user.getBlockPos(), EtherSounds.DEFLECT, user.getSoundCategory(), 0.5f, 1.0f);
//            stack.damage(2, user, entity -> entity.sendToolBreakStatus(entity.getActiveHand()));
//        }
//    }

    public static boolean shieldBlockCheck(Vec3d entityRotation, Vec3d entityPos, Vec3d damagePos, boolean isProjectile) {
        Vec3d damageVec = damagePos.relativize(entityPos).normalize();
        damageVec = new Vec3d(damageVec.x, 0, damageVec.z);
        double cosVal = isProjectile ? 0.0 : -0.866025;
        return damageVec.dotProduct(entityRotation) < cosVal;
    }

    public static Optional<ItemStack> getUsingShield(LivingEntity user) {
        ItemStack activeStack = user.getActiveItem();
        if (!(activeStack.getItem() instanceof EtherShield)) return Optional.empty();
        return Optional.of(activeStack);
    }
}

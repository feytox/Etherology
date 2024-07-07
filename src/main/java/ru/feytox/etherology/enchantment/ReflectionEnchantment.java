package ru.feytox.etherology.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.item.EtherShield;
import ru.feytox.etherology.mixin.EntityHitResultAccessor;
import ru.feytox.etherology.registry.misc.EtherSounds;
import ru.feytox.etherology.registry.misc.TagsRegistry;

import java.util.Optional;

public class ReflectionEnchantment extends Enchantment {

    public ReflectionEnchantment() {
        super(Enchantment.properties(TagsRegistry.ETHER_SHIELDS, 10, 1, Enchantment.constantCost(1), Enchantment.constantCost(21), 3, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
    }

    public static boolean applyReflection(EntityHitResult entityHitResult, ProjectileEntity projectile) {
        World world = projectile.getWorld();
        if (world == null || world.isClient) return true;
        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof LivingEntity target)) return true;
        Optional<ItemStack> optionalShield = EtherShield.getUsingShield(target);
        if (optionalShield.isEmpty()) return true;
        ItemStack shield = optionalShield.get();
        if (EnchantmentHelper.getLevel(EtherEnchantments.REFLECTION, shield) < 1) return true;

        Vec3d targetRotation = target.getRotationVec(1.0F);
        Vec3d targetPos = target.getPos();
        if (!EtherShield.shieldBlockCheck(targetRotation, targetPos, projectile.getPos(), true)) return true;

        Vec3d newVelocity = projectile.getVelocity().negate();
        Entity newProj = projectile.getType().create(world);
        if (newProj == null) return true;

        newProj.copyFrom(projectile);
        newProj.refreshPositionAndAngles(projectile.getX(), projectile.getY(), projectile.getZ(), projectile.getYaw(), projectile.getPitch());
        newProj.setVelocity(newVelocity);
        projectile.setPosition(projectile.getPos().add(0, -10000, 0));
        ((EntityHitResultAccessor) entityHitResult).setEntity(projectile);
        projectile.discard();
        world.spawnEntity(newProj);

        world.playSound(null, target.getBlockPos(), EtherSounds.DEFLECT, target.getSoundCategory(), 0.5f, 1.0f);
        shield.damage(2, target, LivingEntity.getSlotForHand(target.getActiveHand()));
        return false;
    }
}

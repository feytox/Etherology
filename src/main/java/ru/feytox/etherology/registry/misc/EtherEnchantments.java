package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.data.EItemTags;
import ru.feytox.etherology.item.EtherShield;
import ru.feytox.etherology.mixin.EntityHitResultAccessor;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Optional;

@UtilityClass
public class EtherEnchantments {

    public static final RegistryKey<Enchantment> PEAL = of("peal");
    public static final RegistryKey<Enchantment> REFLECTION = of("reflection");

    public static void generateEnchantments(Registerable<Enchantment> context) {
        RegistryEntryLookup<Item> itemLookup = context.getRegistryLookup(RegistryKeys.ITEM);
        register(context, PEAL, Enchantment.builder(Enchantment.definition(itemLookup.getOrThrow(EItemTags.TUNING_MACES), 10, 3, Enchantment.leveledCost(1, 11), Enchantment.leveledCost(21, 11), 3, AttributeModifierSlot.MAINHAND)));
        register(context, REFLECTION, Enchantment.builder(Enchantment.definition(itemLookup.getOrThrow(EItemTags.ETHER_SHIELDS), 10, 1, Enchantment.constantCost(1), Enchantment.constantCost(21), 3, AttributeModifierSlot.MAINHAND, AttributeModifierSlot.OFFHAND)));
    }

    private RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, EIdentifier.of(id));
    }

    public static int getLevel(World world, RegistryKey<Enchantment> enchantmentKey, ItemStack stack) {
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchantmentKey)
                .map(entry -> EnchantmentHelper.getLevel(entry, stack)).orElse(0);
    }

    public static int getLevel(World world, RegistryKey<Enchantment> enchantmentKey, LivingEntity entity) {
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchantmentKey)
                .map(entry -> EnchantmentHelper.getEquipmentLevel(entry, entity)).orElse(0);
    }

    private static void register(Registerable<Enchantment> context, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.getValue()));
    }

    public static boolean applyReflection(EntityHitResult entityHitResult, ProjectileEntity projectile) {
        World world = projectile.getWorld();
        if (world == null || world.isClient) return true;
        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof LivingEntity target)) return true;
        Optional<ItemStack> optionalShield = EtherShield.getUsingShield(target);
        if (optionalShield.isEmpty()) return true;
        ItemStack shield = optionalShield.get();
        if (getLevel(world, REFLECTION, shield) < 1) return true;

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

package ru.feytox.etherology.util.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.item.TuningMaceItem;
import ru.feytox.etherology.item.TwoHandheldSword;
import ru.feytox.etherology.mixin.LivingEntityAccessor;
import ru.feytox.etherology.mixin.PlayerEntityAccessor;
import ru.feytox.etherology.particle.effects.ScalableParticleEffect;
import ru.feytox.etherology.registry.misc.EtherEnchantments;
import ru.feytox.etherology.registry.misc.EtherSounds;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.delayedTask.DelayedTask;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShockwaveUtil {

    public static boolean onAttack(PlayerEntity attacker, Entity target) {
        if (!target.isAttackable()) return false;
        float cooldown = attacker.getAttackCooldownProgress(0.5f);
        if (cooldown < 0.9) return false;
        if (!TwoHandheldSword.isUsing(attacker, TuningMaceItem.class)) return false;

        World world = attacker.getWorld();
        DamageSource damageSource = attacker.getDamageSources().playerAttack(attacker);
        spawnResonationParticle(world, target);
        playAttackSound(world, attacker, target);

        Vec3d shockPos = target.getPos();
        Box attackBox = Box.of(shockPos, 6.0, 2.0, 6.0);
        List<LivingEntity> attackedEntities = world.getNonSpectatingEntities(LivingEntity.class, attackBox);
        attackedEntities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(shockPos)));

        boolean moreDamage = attacker.fallDistance > 0.0F && !attacker.isOnGround() && !attacker.isClimbing() && !attacker.isTouchingWater() && !attacker.hasStatusEffect(StatusEffects.BLINDNESS) && !attacker.hasVehicle() && !attacker.isSprinting();
        float baseDamage = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float damage = ((PlayerEntityAccessor) attacker).callGetDamageAgainst(target, baseDamage, damageSource) - baseDamage;
        if (moreDamage) damage *= 1.2f;

        float knockback = ((LivingEntityAccessor) attacker).callGetKnockbackAgainst(target, damageSource);
        if (attacker.isSprinting()) knockback++;

        damage += baseDamage;
        boolean attackedOnce = true;
        LivingEntity firstTarget = null;
        LivingEntity targetForPeal = null;
        float firstTargetHealth = 0.0f;
        for (LivingEntity attackTarget : attackedEntities) {
            if (!attackTarget.isAttackable()) continue;
            if (attackTarget.equals(attacker)) continue;
            if (attackTarget.handleAttack(attacker)) continue;

            Vec3d attackVec = shockPos.subtract(attackTarget.getPos());
            float vecLen = (float) attackVec.length();
            float attackK = Math.max(1 - vecLen / 3, 0);

            Vec3d oldVelocity = attackTarget.getVelocity();
            if (attackedOnce) {
                attackedOnce = false;
                firstTarget = attackTarget;
                firstTargetHealth = attackTarget.getHealth();
                if (!attackTarget.isOnGround()) {
                    attackTarget.setVelocity(attackTarget.getVelocity().multiply(1, 2.5, 1));
                }
            } else {
                damage *= 0.25f * attackK * attackK;
            }

            if (targetForPeal == null || targetForPeal.isDead()) {
                targetForPeal = attackTarget;
            }

            tryAttack(attacker, attackTarget, damage, knockback, attackVec, vecLen, attackK, oldVelocity);
        }

        trySchedulePeal(world, attacker, targetForPeal, shockPos);
        postShockWave(attacker, firstTarget, world, damageSource, firstTargetHealth);
        return true;
    }

    private static void postShockWave(PlayerEntity attacker, LivingEntity firstTarget, World world, DamageSource damageSource, float firstTargetHealth) {
        attacker.setVelocity(attacker.getVelocity().multiply(0.6, 1.0, 0.6));
        attacker.setSprinting(false);
        if (firstTarget == null) return;

        attacker.onAttacking(firstTarget);
        ItemStack handStack = attacker.getMainHandStack();
        if (!world.isClient && !handStack.isEmpty()) {
            handStack.postHit(firstTarget, attacker);
            if (handStack.isEmpty()) {
                attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

        float m = firstTargetHealth - firstTarget.getHealth();
        attacker.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0F));

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, firstTarget.getX(), firstTarget.getBodyY(0.5), firstTarget.getZ(), (int) (m * 0.5), 0.1, 0.0, 0.1, 0.2);
            EnchantmentHelper.onTargetDamaged(serverWorld, firstTarget, damageSource);
        }

        attacker.addExhaustion(0.1f);
        attacker.resetLastAttackedTicks();
    }

    private static void tryAttack(PlayerEntity attacker, LivingEntity target, double damage, float knockback, Vec3d attackVec, double vecLen, double attackK, Vec3d oldVelocity) {
        boolean wasDamaged = target.damage(attacker.getDamageSources().playerAttack(attacker), (float) damage);
        if (!wasDamaged) return;

        if (knockback > 0) {
            double knockSin = attackVec.x / vecLen;
            double knockCos = attackVec.z / vecLen;
            target.takeKnockback(0.6 * knockback * attackK, knockSin, knockCos);
        }

        if (target instanceof ServerPlayerEntity serverPlayerTarget && target.velocityModified) {
            serverPlayerTarget.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
            target.velocityModified = false;
            target.setVelocity(oldVelocity);
        }
    }

    private static void spawnResonationParticle(World world, Entity target) {
        if (world.isClient) return;
        val effect = new ScalableParticleEffect(EtherParticleTypes.RESONATION, 1.0f);
        Vec3d targetCenter = target.getBoundingBox().getCenter();
        effect.spawnParticles(world, 1, 0.05, targetCenter);
    }

    private static void playAttackSound(World world, PlayerEntity attacker, Entity target) {
        if (target == null || world.isClient) return;
        float pitch = 0.9f + world.random.nextFloat() * 0.2f;
        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();
        world.playSound(null, x, y, z, EtherSounds.TUNING_MACE, attacker.getSoundCategory(), 0.5f, pitch);
    }

    private static void trySchedulePeal(World world, PlayerEntity attacker, Entity target, Vec3d shockPos) {

        int pealLevel = EtherEnchantments.getLevel(world, EtherEnchantments.PEAL, attacker);
        if (world.isClient || pealLevel <= 0) return;

        DelayedTask.createTaskWithMs(world, 600, () -> {
            boolean result = activatePeal((ServerWorld) world, attacker, target.getType(), shockPos, pealLevel);
            if (result) world.playSound(null, target.getBlockPos(), EtherSounds.THUNDER_ZAP, attacker.getSoundCategory(), 1.0f, 1f);
        });
    }

    private static <T extends Entity> boolean activatePeal(ServerWorld world, PlayerEntity attacker, EntityType<T> targetType, Vec3d shockPos, int pealLevel) {
        double diameter = pealLevel / 0.3d;
        int maxCount = pealLevel + 2;
        Box pealBox = Box.of(shockPos, diameter, 3, diameter);
        List<T> pealEntities = world.getEntitiesByType(targetType, pealBox, entity -> !entity.equals(attacker)).stream()
                .filter(entity -> entity.isAttackable() && entity.isAlive() && !entity.handleAttack(attacker))
                .sorted(Comparator.comparing(entity -> entity.squaredDistanceTo(attacker)))
                .limit(maxCount)
                .collect(Collectors.toCollection(ObjectArrayList::new));
        if (pealEntities.isEmpty()) return false;

        pealEntities.forEach(target -> {
            spawnLightningParticle(world, target);
            target.damage(target.getDamageSources().playerAttack(attacker), pealLevel);
        });
        return true;
    }

    private static void spawnLightningParticle(ServerWorld world, Entity target) {
        float scale = target.getWidth() / 0.5f;
        val effect = new ScalableParticleEffect(EtherParticleTypes.LIGHTNING_BOLT, scale);
        effect.spawnParticles(world, 1, 0.05, target.getBoundingBox().getCenter());
    }
}

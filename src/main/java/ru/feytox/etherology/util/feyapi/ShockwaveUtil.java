package ru.feytox.etherology.util.feyapi;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enchantment.PealEnchantment;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.registry.util.EtherSounds;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ShockwaveUtil {

    // TODO: 03.06.2023 make more modular
    public static boolean onFullAttack(PlayerEntity attacker) {
        if (!HammerItem.checkHammer(attacker)) return false;
        World world = attacker.getWorld();
        Vec3d shockPos = getShockPos(attacker.getYaw(), attacker.getPos());

        Box attackBox = Box.of(shockPos, 8.0, 2, 8.0);
        List<LivingEntity> attackedEntities = world.getNonSpectatingEntities(LivingEntity.class, attackBox);
        attackedEntities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(shockPos)));

        float f = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        boolean moreDamage = attacker.fallDistance > 0.0F && !attacker.isOnGround() && !attacker.isClimbing() && !attacker.isTouchingWater() && !attacker.hasStatusEffect(StatusEffects.BLINDNESS) && !attacker.hasVehicle() && !attacker.isSprinting();
        if (moreDamage) {
            f *= 1.5f;
        }
        float knockback = EnchantmentHelper.getKnockback(attacker);
        if (attacker.isSprinting()) knockback++;

        boolean isFirstEntity = true;
        LivingEntity firstTarget = null;
        float firstTargetHealth = 0.0f;
        for (LivingEntity target : attackedEntities) {
            if (!target.isAttackable()) continue;
            if (target.equals(attacker)) continue;
            if (target.handleAttack(attacker)) continue;

            Vec3d dVec = shockPos.subtract(target.getPos());
            double vecLen = dVec.length();
            double attackK = 1 - vecLen / 4;
            float g = EnchantmentHelper.getAttackDamage(attacker.getMainHandStack(), target.getGroup());
            f += g;

            Vec3d oldVelocity = target.getVelocity();
            if (isFirstEntity) {
                isFirstEntity = false;
                firstTarget = target;
                firstTargetHealth = target.getHealth();
                if (!target.isOnGround()) {
                    target.setVelocity(target.getVelocity().multiply(1, 2.5, 1));
                }
            } else {
                f *= 0.5f * attackK;
            }

            boolean wasDamaged = target.damage(DamageSource.player(attacker), f);
            if (!wasDamaged) continue;

            if (knockback > 0) {
                double knockSin = dVec.x / vecLen;
                double knockCos = dVec.z / vecLen;
                target.takeKnockback(0.75 * knockback * attackK, knockSin, knockCos);
            }

            if (target instanceof ServerPlayerEntity serverPlayerTarget && target.velocityModified) {
                serverPlayerTarget.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                target.velocityModified = false;
                target.setVelocity(oldVelocity);
            }
        }

        int pealLevel = EnchantmentHelper.getEquipmentLevel(PealEnchantment.INSTANCE.get(), attacker);
        if (pealLevel > 0 && firstTarget != null) {
            world.playSound(null, firstTarget.getBlockPos(), EtherSounds.THUNDER_ZAP, attacker.getSoundCategory(), 0.5f, 1f);
            doPealDamage(world, attacker, firstTarget, 2 + pealLevel, null);
        }

        attacker.setVelocity(attacker.getVelocity().multiply(0.6, 1.0, 0.6));
        attacker.setSprinting(false);

        if (firstTarget == null) return true;

        attacker.onAttacking(firstTarget);
        EnchantmentHelper.onUserDamaged(firstTarget, attacker);
        EnchantmentHelper.onTargetDamaged(attacker, firstTarget);

        ItemStack handStack = attacker.getMainHandStack();
        if (!world.isClient && !handStack.isEmpty()) {
            handStack.postHit(firstTarget, attacker);
            if (handStack.isEmpty()) {
                firstTarget.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

        float m = firstTargetHealth - firstTarget.getHealth();
        attacker.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0F));

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, firstTarget.getX(), firstTarget.getBodyY(0.5), firstTarget.getZ(), (int) (m * 0.5), 0.1, 0.0, 0.1, 0.2);
        }

        attacker.addExhaustion(0.1f);

        float pitchVal = 0.9f + world.random.nextFloat() * 0.2f;
        Executor delayedExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS);
        double x = firstTarget.getX();
        double y = firstTarget.getY();
        double z = firstTarget.getZ();
        CompletableFuture.runAsync(() ->
                world.playSound(null, x, y, z, EtherSounds.HAMMER_DAMAGE, attacker.getSoundCategory(), 0.5f, pitchVal), delayedExecutor);
        return true;
    }

    private static void doPealDamage(World world, PlayerEntity attacker, Entity target, float damage, @Nullable Map<Integer, Boolean> memo) {
        if (memo == null) memo = new HashMap<>();
        Box pealBox = Box.of(target.getPos(), 6, 2, 6);
        List<? extends Entity> pealedEntities = world.getEntitiesByType(target.getType(), pealBox, EntityPredicates.EXCEPT_SPECTATOR);

        for (Entity pealTarget : pealedEntities) {
            if (!pealTarget.isAttackable()) continue;
            if (pealTarget.equals(attacker)) continue;
            if (pealTarget.handleAttack(attacker)) continue;
            if (memo.containsKey(pealTarget.getId())) continue;
            if (memo.size() >= 7) break;

            pealTarget.damage(DamageSource.player(attacker), damage);
            memo.put(pealTarget.getId(), true);
            doPealDamage(world, attacker, pealTarget, damage, memo);
        }

    }

    public static Vec3d getShockPos(float playerYaw, Vec3d playerPos) {
        float yawAngle = -playerYaw * 0.017453292F;
        Vec3d attackVec = new Vec3d(MathHelper.sin(yawAngle), 0, MathHelper.cos(yawAngle));
        return playerPos.add(attackVec.multiply(1.5)).add(0, 0.025, 0);
    }
}

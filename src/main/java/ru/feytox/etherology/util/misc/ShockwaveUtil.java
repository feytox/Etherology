package ru.feytox.etherology.util.misc;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.enchantment.PealEnchantment;
import ru.feytox.etherology.item.TuningMaceItem;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class ShockwaveUtil {

    public static boolean onAttack(PlayerEntity attacker) {
        float cooldown = attacker.getAttackCooldownProgress(0.0f);
        if (cooldown < 0.9) return false;

        if (!(attacker.getMainHandStack().getItem() instanceof TuningMaceItem)) return false;
        World world = attacker.getWorld();
        Vec3d shockPos = getShockPos(attacker.getYaw(), attacker.getPos());

        Box attackBox = Box.of(shockPos, 6.0, 2.0, 6.0);
        List<LivingEntity> attackedEntities = world.getNonSpectatingEntities(LivingEntity.class, attackBox);
        attackedEntities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(shockPos)));

        double damage = attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        boolean moreDamage = attacker.fallDistance > 0.0F && !attacker.isOnGround() && !attacker.isClimbing() && !attacker.isTouchingWater() && !attacker.hasStatusEffect(StatusEffects.BLINDNESS) && !attacker.hasVehicle() && !attacker.isSprinting();
        if (moreDamage) {
            damage *= 1.2d;
        }
        float knockback = EnchantmentHelper.getKnockback(attacker);
        if (attacker.isSprinting()) knockback++;

        LivingEntity firstTarget = null;
        LivingEntity targetForPeal = null;
        float firstTargetHealth = 0.0f;
        for (int i = 0, size = attackedEntities.size(); i < size; i++) {
            LivingEntity target = attackedEntities.get(i);
            if (!target.isAttackable()) continue;
            if (target.equals(attacker)) continue;
            if (target.handleAttack(attacker)) continue;

            Vec3d attackVec = shockPos.subtract(target.getPos());
            double vecLen = attackVec.length();
            double attackK = Math.max(1 - vecLen / 3, 0);
            damage += EnchantmentHelper.getAttackDamage(attacker.getMainHandStack(), target.getGroup());

            Vec3d oldVelocity = target.getVelocity();
            if (i == 0) {
                firstTarget = target;
                firstTargetHealth = target.getHealth();
                if (!target.isOnGround()) {
                    target.setVelocity(target.getVelocity().multiply(1, 2.5, 1));
                }
            } else {
                damage *= 0.25d * attackK * attackK;
            }

            if (targetForPeal == null || targetForPeal.isDead()) {
                targetForPeal = target;
            }

            tryAttack(attacker, target, damage, knockback, attackVec, vecLen, attackK, oldVelocity);
        }

        PealEnchantment.trySchedulePeal(world, attacker, targetForPeal, shockPos);
        postShockWave(attacker, firstTarget, world, firstTargetHealth);
        return true;
    }

    private static void postShockWave(PlayerEntity attacker, LivingEntity firstTarget, World world, float firstTargetHealth) {
        attacker.setVelocity(attacker.getVelocity().multiply(0.6, 1.0, 0.6));
        attacker.setSprinting(false);
        if (firstTarget == null) return;

        attacker.onAttacking(firstTarget);
        EnchantmentHelper.onUserDamaged(firstTarget, attacker);
        EnchantmentHelper.onTargetDamaged(attacker, firstTarget);

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
        }

        attacker.addExhaustion(0.1f);
        attacker.resetLastAttackedTicks();
    }

    private static void tryAttack(PlayerEntity attacker, LivingEntity target, double damage, float knockback, Vec3d attackVec, double vecLen, double attackK, Vec3d oldVelocity) {
        boolean wasDamaged = target.damage(DamageSource.player(attacker), (float) damage);
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

    public static Vec3d getShockPos(float playerYaw, Vec3d playerPos) {
        float yawAngle = -playerYaw * 0.017453292F;
        Vec3d attackVec = new Vec3d(MathHelper.sin(yawAngle), 0, MathHelper.cos(yawAngle));
        return playerPos.add(attackVec.multiply(1.5)).add(0, 0.025, 0);
    }
}

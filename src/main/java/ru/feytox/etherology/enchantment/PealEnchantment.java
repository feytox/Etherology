package ru.feytox.etherology.enchantment;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import ru.feytox.etherology.enchantment.target.TuningMaceEnchantmentTarget;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.delayedTask.DelayedTask;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PealEnchantment extends Enchantment {
    public static final Supplier<PealEnchantment> INSTANCE = Suppliers.memoize(PealEnchantment::new);

    private PealEnchantment() {
        super(Rarity.RARE, TuningMaceEnchantmentTarget.INSTANCE.get(), new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public static void trySchedulePeal(World world, PlayerEntity attacker, Entity target, Vec3d shockPos) {
        int pealLevel = EnchantmentHelper.getEquipmentLevel(INSTANCE.get(), attacker);
        if (world.isClient || pealLevel <= 0) return;

        DelayedTask.createTaskWithMs(world, 600, () -> {
            boolean result = activatePeal((ServerWorld) world, attacker, target.getType(), shockPos, pealLevel);
            if (result) world.playSound(null, target.getBlockPos(), EtherSounds.THUNDER_ZAP, attacker.getSoundCategory(), 0.5f, 1f);
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
            spawnElectricity(world, target.getBoundingBox().getCenter());
            target.damage(DamageSource.player(attacker), pealLevel);
        });
        return true;
    }

    private static void spawnElectricity(ServerWorld world, Vec3d pos) {
        Random random = world.getRandom();
        val effect = ElectricityParticleEffect.of(random, ElectricitySubtype.PEAL);
        effect.spawnParticles(world, random.nextBetween(3, 6), 0.5, pos);
    }
}

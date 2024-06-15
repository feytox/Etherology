package ru.feytox.etherology.magic.ether;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.CopyableComponent;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

@RequiredArgsConstructor
public class EtherComponent implements ComponentV3, CopyableComponent<EtherComponent>, ServerTickingComponent, AutoSyncedComponent {

    private static final float EXHAUSTION_1 = 5.0f;
    private static final float EXHAUSTION_2 = 3.0f;
    private static final float EXHAUSTION_3 = 2.0f;
    private static final float EXHAUSTION_4 = 0.1f;
    private static final int EXHAUSTION_1_TICKS = 25*20;
    private static final int EXHAUSTION_2_TICKS = 15*20;
    private static final int EXHAUSTION_3_TICKS = 10*20;
    private static final int EXHAUSTION_4_TICKS = 2*20;
    private static final int TICK_RATE = 40;

    private final LivingEntity entity;
    @Getter @Setter
    private float points = 20.0f;
    @Getter @Setter
    private float maxPoints = 20.0f;
    @Getter @Setter
    private float pointsRegen = 0.001f;
    private boolean hasCurse = false;

    public static boolean isEnough(LivingEntity entity, float etherCost) {
        val optionalData = EtherologyComponents.ETHER.maybeGet(entity);
        return optionalData.filter(etherComponent -> etherComponent.points >= etherCost).isPresent();
    }

    /**
     * @return true if ether decremented, otherwise - false
     */
    public static boolean decrement(LivingEntity entity, float value) {
        val optionalData = EtherologyComponents.ETHER.maybeGet(entity);
        if (optionalData.isEmpty()) return false;

        val data = optionalData.get();
        if (data.points < value) return false;

        data.points -= value;
        data.sync();
        return true;
    }

    @Override
    public void serverTick() {
        if (entity.world == null) return;

        tickRegeneration(entity.world);
        tickExhaustion(entity.world);
    }

    private void tickRegeneration(World world) {
        if (world.getTime() % TICK_RATE != 0) return;
        if (points >= maxPoints) return;
        points = Math.min(maxPoints, points + pointsRegen * TICK_RATE);
        EtherologyComponents.ETHER.sync(entity);
    }

    private void tickExhaustion(World world) {
        if (hasCurse) tickCurse(world);
        if (points > EXHAUSTION_1) return;
        if (world.getTime() % EXHAUSTION_1_TICKS == 0) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 150));
        }

        if (points > EXHAUSTION_2) return;
        if (world.getTime() % EXHAUSTION_2_TICKS == 0) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 150));
        }

        if (points > EXHAUSTION_3) return;
        if (world.getTime() % EXHAUSTION_3_TICKS == 0) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100));
        }

        if (points > EXHAUSTION_4 || hasCurse) return;
        hasCurse = true;
        sync();
    }

    private void tickCurse(World world) {
        if (points >= EXHAUSTION_3) {
            hasCurse = false;
            sync();
            return;
        }

        if (world.getTime() % EXHAUSTION_4_TICKS != 0) return;
        float health = entity.getHealth();
        if (health <= 2.0f) return;

        float damage = health > 3.0f ? health / 1.5f : 1.0f;
        entity.damage(DamageSource.MAGIC, damage);
    }

    public static boolean hasCurse(PlayerEntity player) {
        return EtherologyComponents.ETHER.maybeGet(player)
                .map(etherComponent -> etherComponent.hasCurse).orElse(false);
    }

    private void sync() {
        EtherologyComponents.ETHER.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        points = tag.getFloat("points");
        maxPoints = tag.getFloat("max_points");
        pointsRegen = tag.getFloat("points_regen");
        hasCurse = tag.getBoolean("has_curse");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("points", points);
        tag.putFloat("max_points", maxPoints);
        tag.putFloat("points_regen", pointsRegen);
        tag.putBoolean("has_curse", hasCurse);
    }

    @Override
    public void copyFrom(EtherComponent other) {
        NbtCompound tag = new NbtCompound();
        other.writeToNbt(tag);
        this.readFromNbt(tag);
    }
}

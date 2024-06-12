package ru.feytox.etherology.magic.ether;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.CopyableComponent;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

@RequiredArgsConstructor
public class EtherComponent implements ComponentV3, CopyableComponent<EtherComponent>, ServerTickingComponent, AutoSyncedComponent {

    private static final float EXHAUSTION_1 = 5.0f;
    private static final float EXHAUSTION_2 = 2.5f;
    private static final float EXHAUSTION_3 = 1.0f;
    private static final int TICK_RATE = 40;

    private final LivingEntity entity;
    private float points = 20.0f;
    private float maxPoints = 20.0f;
    private float pointsRegen = 0.001f;

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
        EtherologyComponents.ETHER.sync(entity);
        return true;
    }

    @Override
    public void serverTick() {
        if (entity.world == null) return;
        if (entity.world.getTime() % TICK_RATE != 0) return;

        tickRegeneration();
        tickExhaustion();
    }

    private void tickRegeneration() {
        if (points >= maxPoints) return;
        points = Math.min(maxPoints, points + pointsRegen * TICK_RATE);
        EtherologyComponents.ETHER.sync(entity);
    }

    private void tickExhaustion() {
        if (points >= EXHAUSTION_1) return;

        if (points < EXHAUSTION_1 && points >= EXHAUSTION_2) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 1));
        } else if (points < EXHAUSTION_2 && points >= EXHAUSTION_3) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 2));
        } else if (points < EXHAUSTION_3) {
            float multiplier = entity.world.getDifficulty().getId() + 1;
            entity.damage(DamageSource.MAGIC, multiplier);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 3));
        }
    }

    public static boolean isExhaustion(PlayerEntity player) {
        val optionalData = EtherologyComponents.ETHER.maybeGet(player);
        return optionalData.filter(data -> data.points < EXHAUSTION_1).isPresent();
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        points = tag.getFloat("points");
        maxPoints = tag.getFloat("max_points");
        pointsRegen = tag.getFloat("points_regen");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("points", points);
        tag.putFloat("max_points", maxPoints);
        tag.putFloat("points_regen", pointsRegen);
    }

    @Override
    public void copyFrom(EtherComponent other) {
        NbtCompound tag = new NbtCompound();
        other.writeToNbt(tag);
        this.readFromNbt(tag);
    }
}

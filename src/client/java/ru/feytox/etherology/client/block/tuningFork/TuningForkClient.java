package ru.feytox.etherology.client.block.tuningFork;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.block.tuningFork.TuningFork;
import ru.feytox.etherology.block.tuningFork.TuningForkBlockEntity;
import ru.feytox.etherology.particle.effects.ScalableParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

@UtilityClass
public class TuningForkClient {

    private static final int PARTICLE_TICK_RATE = 15;

    public static void clientTick(TuningForkBlockEntity blockEntity, ClientWorld world, BlockPos blockPos, BlockState state) {
        if (!blockEntity.isResonating() || world.getTime() % PARTICLE_TICK_RATE != 0) return;

        var offset = Vec3d.of(state.get(TuningFork.VERTICAL_FACING).getVector()).multiply(0.25d);
        var effect = new ScalableParticleEffect(EtherParticleTypes.RESONATION, 0.5f);
        effect.spawnParticles(world, 1, 0, blockPos.toCenterPos().add(offset));
    }
}

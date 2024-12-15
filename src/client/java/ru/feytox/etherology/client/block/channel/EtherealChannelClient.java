package ru.feytox.etherology.client.block.channel;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.block.etherealChannel.EtherealChannelBlockEntity;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

import static ru.feytox.etherology.block.etherealChannel.EtherealChannel.ACTIVATED;

@UtilityClass
public class EtherealChannelClient {

    public static void clientTick(EtherealChannelBlockEntity blockEntity, ClientWorld world, BlockPos blockPos, BlockState state) {
        if (state.get(ACTIVATED)) return;

        Direction outputDirection = blockEntity.getOutputSide();
        if (outputDirection == null) return;

        BlockPos outputPos = blockEntity.isCrossEvaporating() ? blockEntity.getPos().up() : blockEntity.getPos();
        if (!blockEntity.isEvaporating()) return;

        if (blockEntity.isCrossEvaporating() && world.getBlockEntity(outputPos) instanceof EtherStorage consumer) {
            if (consumer.spawnCrossParticles(outputPos, world, outputDirection)) return;
        }

        spawnParticles(outputPos, world, outputDirection);
    }

    public static void spawnParticles(BlockPos pos, ClientWorld world, Direction direction) {
        if (world.getTime() % 4 != 0) return;
        Random random = world.getRandom();

        Vec3d channelVec = Vec3d.of(direction.getVector());
        Vec3d startPos = pos.toCenterPos().add(channelVec.multiply(0.5d));
        val particleType = random.nextFloat() < 0.25 ? EtherParticleTypes.ETHER_STAR : EtherParticleTypes.ETHER_DOT;
        val effect = new MovingParticleEffect(particleType, channelVec);
        effect.spawnParticles(world, random.nextBetween(1, 2), 0, startPos);
    }
}

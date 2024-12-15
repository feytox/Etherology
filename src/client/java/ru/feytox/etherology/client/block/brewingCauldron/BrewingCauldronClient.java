package ru.feytox.etherology.client.block.brewingCauldron;

import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlock;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlockEntity;
import ru.feytox.etherology.client.util.ClientTickableBlock;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

import static ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlockEntity.getWaterPos;

public class BrewingCauldronClient extends ClientTickableBlock<BrewingCauldronBlockEntity> {

    @Nullable
    private BrewingCauldronSoundInstance soundInstance = null;

    public BrewingCauldronClient(BrewingCauldronBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (!BrewingCauldronBlock.isFilled(state)) return;

        tickBubbleParticles(world, state, blockPos);
        tickCorruptionParticles(world, state, blockPos);
        tickSound(world, blockPos);
    }

    private void tickSound(ClientWorld world, BlockPos pos) {
        if (blockEntity.getTemperature() < 100) return;
        val client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (world.getTime() % 3 != 0) return;
        if (world.getRandom().nextFloat() > 0.25f) return;

        if (soundInstance == null && client.player.squaredDistanceTo(pos.toCenterPos()) < 36) {
            soundInstance = new BrewingCauldronSoundInstance(blockEntity, client.player);
            client.getSoundManager().play(soundInstance);
            return;
        }

        if (soundInstance == null) return;
        if (soundInstance.isDone() || !client.getSoundManager().isPlaying(soundInstance)) soundInstance = null;
    }

    private void tickBubbleParticles(ClientWorld world, BlockState state, BlockPos pos) {
        if (world.getTime() % 3 != 0) return;
        if (blockEntity.getTemperature() < 100) return;

        Random random = world.getRandom();
        for (int i = 0; i < random.nextBetween(1, 4); i++) {
            SimpleParticleType effect = random.nextBoolean() ? ParticleTypes.BUBBLE : ParticleTypes.BUBBLE_POP;
            Vec3d start = getWaterPos(state).add(Vec3d.of(pos));
            start = start.add(FeyParticleEffect.getRandomPos(random, 0.25, 0.05, 0.25));
            world.addParticle(effect, start.x, start.y, start.z, 0, 0.001, 0);
        }
    }

    private void tickCorruptionParticles(ClientWorld world, BlockState state, BlockPos pos) {
        if (blockEntity.getTemperature() < 100) return;
        int count = blockEntity.getAspects().sum().orElse(0);
        if (count == 0) return;
        if (world.getTime() % Math.max(MathHelper.floor(250.0f / count + 2), 1) != 0) return;
        if (world.getRandom().nextFloat() < 0.5) return;

        Vec3d start = getWaterPos(state).add(Vec3d.of(pos));
        val effect = new SimpleParticleEffect(EtherParticleTypes.HAZE);
        effect.spawnParticles(world, 1, 0.1, start);
    }
}

package ru.feytox.etherology.client.block.matrix;

import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.matrix.MatrixBlockEntity;
import ru.feytox.etherology.client.util.ClientTickableBlock;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.particle.subtype.ElectricitySubtype;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

import java.util.Optional;

import static ru.feytox.etherology.block.matrix.MatrixBlockEntity.IDLE_ANIM;
import static ru.feytox.etherology.block.matrix.MatrixState.IDLE;

public class MatrixBlockClient extends ClientTickableBlock<MatrixBlockEntity> {

    @Nullable
    private MatrixSoundInstance soundInstance = null;
    private boolean animationsRefreshed = false;

    public MatrixBlockClient(MatrixBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        tickSound();
        tickAnimations(state);
        tickClientParticles(world, state);
    }

    /**
     * Tick the sound if matrix is working.
     */
    private void tickSound() {
        val client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (soundInstance == null && client.player.squaredDistanceTo(blockEntity.getCenterPos()) < 36) {
            soundInstance = new MatrixSoundInstance(blockEntity, client.player);
            client.getSoundManager().play(soundInstance);
            return;
        }

        if (soundInstance == null) return;
        if (soundInstance.isDone()) soundInstance = null;
    }

    private void tickAnimations(BlockState state) {
        if (animationsRefreshed) return;

        val matrixState = blockEntity.getMatrixState(state);
        if (matrixState.equals(IDLE)) IDLE_ANIM.triggerOnce(blockEntity);
        else blockEntity.getActiveAnimations().forEach(blockEntity::triggerOnce);

        animationsRefreshed = true;
    }

    private void tickClientParticles(ClientWorld world, BlockState state) {
        val matrixState = blockEntity.getMatrixState(state);
        Vec3d centerPos = blockEntity.getCenterPos();
        switch (matrixState) {
            case CONSUMING -> {
                Optional<? extends LivingEntity> match = blockEntity.findClosestEntity(world, centerPos);
                match.ifPresent(entity -> spawnConsumingParticle(world, entity, centerPos));
                if (world.getTime() % 2 == 0 && world.getRandom().nextBoolean()) {
                    world.playSound(centerPos.x, centerPos.y, centerPos.z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.066f, 0.7f * world.getRandom().nextFloat() + 0.55f, true);
                }
            }
            case RESETTING -> spawnSphereParticles(world);
            case DECRYPTING_START, RESULTING -> spawnElectricityParticles(world);
            case DECRYPTING -> {
                spawnElectricityParticles(world);
                spawnSphereParticles(world);
            }
        }
    }

    private void spawnSphereParticles(ClientWorld world) {
        Random random = world.getRandom();
        Vec3d centerPos = blockEntity.getCenterPos();
        Vec3d randomVec = new Vec3d(0.4 + random.nextDouble()*0.4, 0.4 + random.nextDouble()*0.4, 0.4 + random.nextDouble()*0.4);
        randomVec = randomVec.multiply(random.nextInt(2)*2 - 1, random.nextInt(2)*2 - 1, random.nextInt(2)*2 - 1);
        Vec3d startPos = centerPos.add(randomVec);

        val effect = new MovingParticleEffect(EtherParticleTypes.ARMILLARY_SPHERE, randomVec);
        effect.spawnParticles(world, 2, 0, startPos);
    }

    private void spawnElectricityParticles(ClientWorld world) {
        Random random = world.getRandom();
        val effect = ElectricityParticleEffect.of(random, ElectricitySubtype.MATRIX);
        effect.spawnParticles(world, 1, 0.45, blockEntity.getCenterPos());
    }

    /**
     * Spawns a consuming particle effect from a given entity at a specific position.
     *
     * @param world     the world in which to spawn the particles
     * @param entity    the entity from which to spawn the particles
     * @param centerPos the matrix center position
     */
    private void spawnConsumingParticle(World world, LivingEntity entity, Vec3d centerPos) {
        if (world.getTime() % 2 == 0) return;
        val effect = new MovingParticleEffect(EtherParticleTypes.VITAL, centerPos);
        effect.spawnParticles(world, 1, 0.1, entity.getBoundingBox().getCenter());
    }
}

package ru.feytox.etherology.client.block.etherealSocket;

import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.block.etherealSocket.EtherealSocketBlockEntity;
import ru.feytox.etherology.client.util.ClientTickableBlock;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.deprecated.EVec3d;

import static net.minecraft.block.FacingBlock.FACING;
import static ru.feytox.etherology.block.etherealSocket.EtherealSocketBlock.WITH_GLINT;

public class EtherealSocketClient extends ClientTickableBlock<EtherealSocketBlockEntity> {

    private Boolean wasWithGlint = null;

    public EtherealSocketClient(EtherealSocketBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        cachePercent();
        tickGlintParticles(world, blockPos, state);
    }

    public void cachePercent() {
        // TODO: 24/03/2023 как-то изменить, чтобы не моргало
        var storedEther = blockEntity.getStoredEther();
        var maxEther = blockEntity.getMaxEther();
        var currentPercent = blockEntity.getCachedPercent();
        blockEntity.setCachedPercent(maxEther == 0 ? currentPercent : storedEther / maxEther);
    }

    private void tickGlintParticles(ClientWorld world, BlockPos pos, BlockState state) {
        var withGlint = state.getOrEmpty(WITH_GLINT).orElse(null);
        if (withGlint == null) return;

        if (wasWithGlint != null && !wasWithGlint && withGlint) {
            spawnGlintParticles(world, pos, state.get(FACING));
        }
        wasWithGlint = withGlint;
    }

    private void spawnGlintParticles(ClientWorld world, BlockPos blockPos, Direction facing) {
        var random = world.getRandom();
        var pos = Vec3d.of(blockPos);
        Vec3d centerPos = blockPos.toCenterPos().subtract(0, 0.3, 0);

        var dxz = 6/16d;
        var dy = 0.25;
        // down direction
        var start = new Vec3d(dxz, dy, dxz);
        var end = new Vec3d(0.25 + dxz, dy, 0.25 + dxz);
        switch (facing) {
            case UP -> {
                start = new Vec3d(dxz, 0.5 + dy, dxz);
                end = new Vec3d(0.25 + dxz, 0.5 + dy, 0.25 + dxz);
            }
            case NORTH -> {
                start = new Vec3d(dxz, dxz, dy);
                end = new Vec3d(0.25 + dxz, 0.25 + dxz, dy);
            }
            case SOUTH -> {
                start = new Vec3d(dxz, dxz, 0.5 + dy);
                end = new Vec3d(0.25 + dxz, 0.25 + dxz, 0.5 + dy);
            }
            case WEST -> {
                start = new Vec3d(dy, dxz, dxz);
                end = new Vec3d(dy, 0.25 + dxz, 0.25 + dxz);
            }
            case EAST -> {
                start = new Vec3d(0.5 + dy, dxz, dxz);
                end = new Vec3d(0.5 + dy, 0.25 + dxz, 0.25 + dxz);
            }
        }

        var particlePoses = EVec3d.aroundSquareOf(pos.add(start), pos.add(end), 0.05d);

        var count = MathHelper.floor(25f * blockEntity.getCachedPercent());
        for (var i = 0; i < count; i++) {
            var j = random.nextInt(particlePoses.size()-1);
            var particlePos = particlePoses.get(j);
            var path = particlePos
                    .subtract(centerPos)
                    .multiply(random.nextDouble() * 1);
            path = particlePos.add(path);
            val effect = new MovingParticleEffect(EtherParticleTypes.GLINT, path);
            effect.spawnParticles(world, 2, 0.01, particlePos);
        }
    }
}

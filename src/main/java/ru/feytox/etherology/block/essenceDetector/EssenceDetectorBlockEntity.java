package ru.feytox.etherology.block.essenceDetector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.seal.EssenceDetector;
import ru.feytox.etherology.magic.seal.EssenceSupplier;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.Optional;

import static ru.feytox.etherology.block.essenceDetector.EssenceDetectorBlock.POWER;
import static ru.feytox.etherology.registry.block.EBlocks.ESSENCE_DETECTOR_BLOCK_ENTITY;

public class EssenceDetectorBlockEntity extends TickableBlockEntity implements EssenceDetector {

    @Nullable
    private EssenceSupplier cachedSeal;

    public EssenceDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ESSENCE_DETECTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (world.getTime() % 20 == 0) tickSealCheck(world, blockPos, state);
    }

    public void tickSealCheck(ServerWorld world, BlockPos blockPos, BlockState state) {
        float fillPercent = getAndCacheSeal(world, blockPos).map(EssenceSupplier::getFillPercent).orElse(0.0f);
        int i = MathHelper.floor(fillPercent * 15);
        world.setBlockState(blockPos, state.with(POWER, i), Block.NOTIFY_ALL);
    }

    @Override
    public Optional<EssenceSupplier> getCachedSeal() {
        return Optional.ofNullable(cachedSeal);
    }

    @Override
    public void setCachedSeal(EssenceSupplier seal) {
        cachedSeal = seal;
    }
}

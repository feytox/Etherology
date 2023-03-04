package name.uwu.feytox.etherology.blocks.essenceDetector;

import name.uwu.feytox.etherology.magic.zones.EssenceDetector;
import name.uwu.feytox.etherology.magic.zones.EssenceSupplier;
import name.uwu.feytox.etherology.util.feyapi.TickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.ESSENCE_DETECTOR_BLOCK_ENTITY;
import static name.uwu.feytox.etherology.blocks.essenceDetector.EssenceDetectorBlock.POWER;

public class EssenceDetectorBlockEntity extends TickableBlockEntity implements EssenceDetector {
    private EssenceSupplier cachedSupplier = null;

    public EssenceDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ESSENCE_DETECTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {}

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (world.getTime() % 20L == 0) tickSupplier(world, blockPos, state);
    }

    @Override
    public void tickSupplier(ServerWorld world, BlockPos blockPos, BlockState state) {
        float k = getSupplierPercent();
        int i = MathHelper.floor(k * 15);
        world.setBlockState(blockPos, state.with(POWER, i), Block.NOTIFY_ALL);
    }

    @Override
    public float getRadius() {
        return 15;
    }

    @Override
    public BlockPos getDetectablePos() {
        return pos;
    }

    @Nullable
    @Override
    public EssenceSupplier getCachedSupplier() {
        return cachedSupplier;
    }

    @Override
    public void setCachedSupplier(EssenceSupplier supplier) {
        cachedSupplier = supplier;
    }
}

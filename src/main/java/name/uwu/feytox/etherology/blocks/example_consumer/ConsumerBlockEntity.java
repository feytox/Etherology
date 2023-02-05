package name.uwu.feytox.etherology.blocks.example_consumer;

import name.uwu.feytox.etherology.magic.zones.EssenceConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.CONSUMER_BLOCK_ENTITY;

public class ConsumerBlockEntity extends BlockEntity implements EssenceConsumer {
    private BlockPos cachedZonePos = null;
    private int consumingTicks = 0;

    public ConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(CONSUMER_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        ServerWorld serverWorld = (ServerWorld) world;
        ConsumerBlockEntity consumerBlock = (ConsumerBlockEntity) blockEntity;

        consumerBlock.consumingTick(serverWorld);
    }

    public void consumingTick(ServerWorld world) {
        if (consumingTicks++ % 20 != 0) return;

        tickConsume(world);
    }

    @Override
    public float getConsumingValue() {
        return 4;
    }

    @Override
    public float getRadius() {
        return 15;
    }

    @Override
    public BlockPos getConsumerPos() {
        return pos;
    }

    @Nullable
    @Override
    public BlockPos getCachedZonePos() {
        return cachedZonePos;
    }

    @Override
    public void setCachedCorePos(BlockPos blockPos) {
        cachedZonePos = blockPos;
    }

    @Override
    public void increment(float value) {
        System.out.println(value);
    }
}

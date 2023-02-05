package name.uwu.feytox.etherology.blocks.example_consumer;

import name.uwu.feytox.etherology.util.registry.SimpleBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.CONSUMER_BLOCK_ENTITY;

public class ConsumerBlock extends SimpleBlock implements BlockEntityProvider {
    public ConsumerBlock() {
        super("consumer_block", FabricBlockSettings.of(Material.METAL).nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConsumerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != CONSUMER_BLOCK_ENTITY) return null;

        return world.isClient ? null : ConsumerBlockEntity::serverTick;
    }
}

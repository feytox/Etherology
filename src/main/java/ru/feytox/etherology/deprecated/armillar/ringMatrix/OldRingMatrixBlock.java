package ru.feytox.etherology.deprecated.armillar.ringMatrix;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.deprecated.SimpleBlock;

import static ru.feytox.etherology.registry.block.EBlocks.RING_MATRIX_BLOCK_ENTITY;

public class OldRingMatrixBlock extends SimpleBlock implements BlockEntityProvider {
    public OldRingMatrixBlock() {
        super("ring_matrix", FabricBlockSettings.of(Material.METAL).strength(10.0f).nonOpaque());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OldRingMatrixBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

        OldRingMatrixBlockEntity ringMatrix = (OldRingMatrixBlockEntity) world.getBlockEntity(pos);
        if (ringMatrix != null) ringMatrix.setWorld(world);

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0, 0, 0, 0, 0, 0);
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return Block.createCuboidShape(0, 0, 0, 0, 0, 0);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != RING_MATRIX_BLOCK_ENTITY) return null;

        return world.isClient ? null : OldRingMatrixBlockEntity::serverTick;
    }
}

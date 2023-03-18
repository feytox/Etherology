package ru.feytox.etherology.blocks.pedestal;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.registry.SimpleBlock;

import static ru.feytox.etherology.BlocksRegistry.PEDESTAL_BLOCK_ENTITY;

public class PedestalBlock extends SimpleBlock implements BlockEntityProvider {
    private static final VoxelShape BOTTOM_CUBOID =
            Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 3.0, 13.0);
    private static final VoxelShape MIDDLE_CUBOID =
            Block.createCuboidShape(4.0, 3.0, 4.0, 12.0, 11.0, 12.0);
    private static final VoxelShape TOP_CUBOID =
            Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE;

    public PedestalBlock() {
        super("pedestal", FabricBlockSettings.of(Material.STONE).strength(3.0f).nonOpaque());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            PedestalBlockEntity pedestalBlockEntity = (PedestalBlockEntity) world.getBlockEntity(pos);
            if (pedestalBlockEntity != null) {
                pedestalBlockEntity.interact(player, hand);
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
        }

        return ActionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != PEDESTAL_BLOCK_ENTITY) return null;

        return !world.isClient ?
                ((world1, pos, state1, be) -> PedestalBlockEntity.serverTick(world1, pos, state1, (PedestalBlockEntity) be))
                : ((world1, pos, state1, be) -> PedestalBlockEntity.clientTick(world1, pos, state1, (PedestalBlockEntity) be));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PedestalBlockEntity) {
                ItemScatterer.spawn(world, pos, ((PedestalBlockEntity) blockEntity));
                ((PedestalBlockEntity) blockEntity).onBreak(world);
            }
        }
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.union(BOTTOM_CUBOID, MIDDLE_CUBOID, TOP_CUBOID);
    }
}

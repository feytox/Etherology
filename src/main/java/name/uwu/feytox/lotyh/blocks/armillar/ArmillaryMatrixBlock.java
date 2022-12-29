package name.uwu.feytox.lotyh.blocks.armillar;

import name.uwu.feytox.lotyh.util.SimpleBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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

import static name.uwu.feytox.lotyh.BlocksRegistry.ARMILLARY_MATRIX_BLOCK_ENTITY;
import static name.uwu.feytox.lotyh.BlocksRegistry.RING_MATRIX_BLOCK;

public class ArmillaryMatrixBlock extends SimpleBlock implements BlockEntityProvider {
    private static final VoxelShape BOTTOM_CUBOID =
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private static final VoxelShape TOP_CUBOID =
            Block.createCuboidShape(2.0, 8.0, 2.0, 14.0, 12.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE;

    public ArmillaryMatrixBlock() {
        super("armillary_matrix_base", FabricBlockSettings.of(Material.STONE).strength(3.0f).nonOpaque());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ArmillaryMatrixBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

        world.setBlockState(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()), RING_MATRIX_BLOCK.getDefaultState());

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ArmillaryMatrixBlockEntity blockEntity = (ArmillaryMatrixBlockEntity) world.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.interact((ServerWorld) world, player, hand);

                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
        }

        return ActionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient && type == ARMILLARY_MATRIX_BLOCK_ENTITY ?
                ((world1, pos, state1, be) -> ArmillaryMatrixBlockEntity.tick(world1, pos, state1, (ArmillaryMatrixBlockEntity) be))
                : null;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) {
            super.onBreak(world, pos, state, player);
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ArmillaryMatrixBlockEntity) {
            ItemScatterer.spawn(world, pos, ((ArmillaryMatrixBlockEntity) blockEntity));
            ((ArmillaryMatrixBlockEntity) blockEntity).onBreak((ServerWorld) world);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.union(BOTTOM_CUBOID, TOP_CUBOID);
    }
}

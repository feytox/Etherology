package name.uwu.feytox.etherology.blocks.pedestal;

import name.uwu.feytox.etherology.Etherology;
import name.uwu.feytox.etherology.util.NbtCoord;
import name.uwu.feytox.etherology.util.SimpleBlock;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.PEDESTAL_BLOCK_ENTITY;

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

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isClient) return;

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof PedestalBlockEntity pedestal) {
            if (!pedestal.isConsuming()) return;

            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.5;
            double z = pos.getZ() + 0.5;
            NbtCoord center = pedestal.getCenterCoord();
            world.addParticle(Etherology.CONSUMING, x, y, z, center.x, center.y, center.z);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient && type == PEDESTAL_BLOCK_ENTITY ?
                ((world1, pos, state1, be) -> PedestalBlockEntity.tick(world1, pos, state1, (PedestalBlockEntity) be))
                : null;
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

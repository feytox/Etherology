package name.uwu.feytox.etherology.blocks.crucible;

import name.uwu.feytox.etherology.BlocksRegistry;
import name.uwu.feytox.etherology.util.registry.SimpleBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class CrucibleBlock extends SimpleBlock implements BlockEntityProvider {
    private static final VoxelShape RAYCAST_SHAPE =
            createCuboidShape(3.5, 5.0, 3.5, 12.5, 15.1, 12.5);
    protected static final VoxelShape OUTLINE_SHAPE;

    public CrucibleBlock() {
        super("crucible", FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            CrucibleBlockEntity.interact(world, pos, player, hand);
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return type == BlocksRegistry.CRUCIBLE_BLOCK_ENTITY ?
                (world1, pos, state1, be) -> CrucibleBlockEntity.tick(world1, pos, state1, (CrucibleBlockEntity) be) : null;
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(1.5, 0, 1.5, 14.5, 15.1, 14.5),
                VoxelShapes.union(RAYCAST_SHAPE),
                BooleanBiFunction.ONLY_FIRST);
    }
}

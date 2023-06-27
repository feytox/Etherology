package ru.feytox.etherology.block.crucible;

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
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import static ru.feytox.etherology.registry.block.EBlocks.CRUCIBLE_BLOCK_ENTITY;

public class CrucibleBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    private static final VoxelShape RAYCAST_SHAPE =
            createCuboidShape(3.5, 5.0, 3.5, 12.5, 15.1, 12.5);
    protected static final VoxelShape OUTLINE_SHAPE;

    public CrucibleBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != CRUCIBLE_BLOCK_ENTITY) return null;

        return world.isClient ? CrucibleBlockEntity::clientTicker : CrucibleBlockEntity::serverTicker;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)) return ActionResult.FAIL;

        if (world.isClient) crucible.triggerAnim("mixing");
        else StartBlockAnimS2C.sendForTracking(crucible, "mixing", player);

        return ActionResult.SUCCESS;
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "crucible";
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(1.5, 0, 1.5, 14.5, 15.1, 14.5),
                VoxelShapes.union(RAYCAST_SHAPE),
                BooleanBiFunction.ONLY_FIRST);
    }
}

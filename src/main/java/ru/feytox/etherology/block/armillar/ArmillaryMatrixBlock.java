package ru.feytox.etherology.block.armillar;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
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
import ru.feytox.etherology.enums.ArmillaryState;
import ru.feytox.etherology.enums.InstabilityType;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import static ru.feytox.etherology.registry.block.EBlocks.ARMILLARY_MATRIX_BLOCK_ENTITY_NEW;

public class ArmillaryMatrixBlock extends Block implements RegistrableBlock, BlockEntityProvider {

    public static final EnumProperty<ArmillaryState> MATRIX_STATE = EnumProperty.of("matrix_state", ArmillaryState.class);
    public static final EnumProperty<InstabilityType> CRAFT_INSTABILITY = EnumProperty.of("craft_instability", InstabilityType.class);
    private static final VoxelShape OUTLINE_SHAPE;

    public ArmillaryMatrixBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(3.0f).nonOpaque());
        setDefaultState(getDefaultState().with(MATRIX_STATE, ArmillaryState.OFF).with(CRAFT_INSTABILITY, InstabilityType.NULL));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient || !(world.getBlockEntity(pos) instanceof ArmillaryMatrixBlockEntity matrix)) return ActionResult.CONSUME;
        matrix.onHandUse((ServerWorld) world, state, player, hand);
        matrix.syncData((ServerWorld) world);
        return ActionResult.CONSUME;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && !moved) {
            if (world.getBlockEntity(pos) instanceof ArmillaryMatrixBlockEntity matrix) {
                ItemScatterer.spawn(world, pos, matrix);
            }

            super.onStateReplaced(state, world, pos, newState, false);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MATRIX_STATE, CRAFT_INSTABILITY);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!type.equals(ARMILLARY_MATRIX_BLOCK_ENTITY_NEW)) return null;

        return world.isClient ? ArmillaryMatrixBlockEntity::clientTicker : ArmillaryMatrixBlockEntity::serverTicker;
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "armillary_base";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ArmillaryMatrixBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
                Block.createCuboidShape(2.0, 8.0, 2.0, 14.0, 12.0, 14.0)
        );
    }
}

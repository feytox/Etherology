package ru.feytox.etherology.blocks.etherealGenerators.spinner;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.blocks.etherealGenerators.AbstractEtherealGenerator;

import static ru.feytox.etherology.BlocksRegistry.ETHEREAL_SPINNER_BLOCK_ENTITY;

public class EtherealSpinnerBlock extends AbstractEtherealGenerator {
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape UP_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;

    public EtherealSpinnerBlock() {
        super(FabricBlockSettings.of(Material.METAL).nonOpaque(),
                "ethereal_spinner", 30*20, 120*20, 0.1f);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return ETHEREAL_SPINNER_BLOCK_ENTITY;
    }

    @Override
    public VoxelShape getDownShape() {
        return DOWN_SHAPE;
    }

    @Override
    public VoxelShape getUpShape() {
        return UP_SHAPE;
    }

    @Override
    public VoxelShape getNorthShape() {
        return NORTH_SHAPE;
    }

    @Override
    public VoxelShape getSouthShape() {
        return SOUTH_SHAPE;
    }

    @Override
    public VoxelShape getEastShape() {
        return EAST_SHAPE;
    }

    @Override
    public VoxelShape getWestShape() {
        return WEST_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealSpinnerBlockEntity(pos, state);
    }

    static {
        DOWN_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 0, 16, 6, 16),
                createCuboidShape(5, 6, 5, 11, 8, 11),
                BooleanBiFunction.OR
        );
        UP_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 10, 0, 16, 16, 16),
                createCuboidShape(5, 8, 5, 11, 10, 11),
                BooleanBiFunction.OR
        );
        NORTH_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 10, 16, 16, 16),
                createCuboidShape(5, 5, 8, 11, 11, 10),
                BooleanBiFunction.OR
        );
        SOUTH_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 0, 16, 16, 6),
                createCuboidShape(5, 5, 6, 11, 11, 8),
                BooleanBiFunction.OR
        );
        EAST_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 0, 6, 16, 16),
                createCuboidShape(6, 5, 5, 8, 11, 11),
                BooleanBiFunction.OR
        );
        WEST_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(10, 0, 0, 16, 16, 16),
                createCuboidShape(8, 5, 5, 10, 11, 11),
                BooleanBiFunction.OR
        );
    }
}

package ru.feytox.etherology.blocks.essenceDetector;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.registry.SimpleBlock;

import static ru.feytox.etherology.BlocksRegistry.ESSENCE_DETECTOR_BLOCK_ENTITY;

public class EssenceDetectorBlock extends SimpleBlock implements BlockEntityProvider {
    public static final IntProperty POWER = Properties.POWER;
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public EssenceDetectorBlock() {
        super("essence_detector", FabricBlockSettings.of(Material.STONE));
        setDefaultState(getDefaultState().with(POWER, 0));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ESSENCE_DETECTOR_BLOCK_ENTITY) return null;

        return world.isClient ? EssenceDetectorBlockEntity::clientTicker : EssenceDetectorBlockEntity::serverTicker;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EssenceDetectorBlockEntity(pos, state);
    }
}
package ru.feytox.etherology.block.thuja;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

public class ThujaBlock extends AbstractPlantStemBlock implements RegistrableBlock, ThujaShapeController {

    // TODO: 02.03.2024 Consider combining code from ThujaBlock and ThujaPlantBlock to avoid copy-pasting

    public static final EnumProperty<ThujaShape> SHAPE = EnumProperty.of("shape", ThujaShape.class);
    public static final VoxelShape OUTLINE_SHAPE = VoxelShapes.fullCube();

    public ThujaBlock() {
        super(AbstractBlock.Settings.of(Material.PLANT, MapColor.EMERALD_GREEN).ticksRandomly().noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS), Direction.UP, OUTLINE_SHAPE, false, 0.1);
        setDefaultState(getDefaultState()
                .with(SHAPE, ThujaShape.BUSH)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SHAPE);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        return state == null ? null : getThujaPlacementState(state, ctx);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        state = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        return getThujaStateForNeighborUpdate(state, world, pos, neighborPos);
    }

    protected int getGrowthAmount(Random random) {
        return MathHelper.nextInt(random, 0, 18);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int age = state.get(AGE) + getGrowthAmount(random);
        age = Math.min(age, MAX_AGE);
        world.setBlockState(pos, state.with(AGE, age), Block.NOTIFY_LISTENERS);
        if (age != MAX_AGE) return;

        BlockPos blockPos = pos.offset(this.growthDirection);
        int growthLength = this.getGrowthLength(random);

        for(int k = 0; k < growthLength && this.chooseStemState(world.getBlockState(blockPos)); ++k) {
            world.setBlockState(blockPos, state.with(AGE, 0));
            blockPos = blockPos.offset(this.growthDirection);
        }
    }

    @Override
    protected int getGrowthLength(Random random) {
        return MathHelper.nextInt(random, 1, 2);
    }

    @Override
    protected boolean chooseStemState(BlockState state) {
        return state.isAir();
    }

    @Override
    protected Block getPlant() {
        return DecoBlocks.THUJA_PLANT;
    }

    @Override
    public String getBlockId() {
        return "thuja";
    }
}

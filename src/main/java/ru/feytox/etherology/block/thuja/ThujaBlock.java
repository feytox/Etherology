package ru.feytox.etherology.block.thuja;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
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
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.misc.RegistrableBlock;

public class ThujaBlock extends AbstractPlantStemBlock implements RegistrableBlock, ThujaShapeController {

    // TODO: 02.03.2024 Consider combining code from ThujaBlock and ThujaPlantBlock to avoid copy-pasting

    private static final MapCodec<ThujaBlock> CODEC = MapCodec.unit(ThujaBlock::new);
    public static final EnumProperty<ThujaShape> SHAPE = EnumProperty.of("shape", ThujaShape.class);
    public static final VoxelShape OUTLINE_SHAPE = VoxelShapes.fullCube();
    private static final int MAX_LENGTH = 5;

    public ThujaBlock() {
        super(Settings.create().mapColor(MapColor.EMERALD_GREEN).ticksRandomly().noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS), Direction.UP, OUTLINE_SHAPE, false, 0.1);
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
    protected MapCodec<? extends AbstractPlantStemBlock> getCodec() {
        return CODEC;
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
        int blocksCount = 1 + countBelow(world, pos) + countAbove(world, pos);
        if (blocksCount >= MAX_LENGTH) return;

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

    private int countBelow(ServerWorld world, BlockPos pos) {
        int result = 0;
        while(result < 4 && world.getBlockState(pos.down(result+1)).getBlock() instanceof ThujaShapeController) {
            result++;
        }
        return result;
    }

    private int countAbove(ServerWorld world, BlockPos pos) {
        int result = 0;
        while(result < 4 && world.getBlockState(pos.up(result+1)).getBlock() instanceof ThujaShapeController) {
            result++;
        }
        return result;
    }

    @Override
    protected boolean canAttachTo(BlockState state) {
        return state.isIn(BlockTags.DIRT) || state.getBlock() instanceof ThujaShapeController;
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

    @Override // because super method is protected
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return super.canPlaceAt(state, world, pos);
    }
}

package ru.feytox.etherology.blocks.beamer;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import ru.feytox.etherology.util.registry.RegistrableBlock;

public class BeamerBlock extends PlantBlock implements Fertilizable, RegistrableBlock {
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;
    private static final VoxelShape[] AGE_TO_SHAPE;

    public BeamerBlock() {
        super(FabricBlockSettings.of(Material.PLANT).nonOpaque().ticksRandomly().sounds(BlockSoundGroup.GRASS).noCollision().breakInstantly());
        this.setDefaultState(getDefaultState().with(AGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[state.get(AGE)];
    }

    public boolean isMature(BlockState state) {
        return state.get(AGE) == MAX_AGE;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !isMature(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(100) == 0 && !isMature(state)) {
            world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return !isMature(state);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 0, 2);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = state.get(AGE) + this.getGrowthAmount(world);
        i = Math.min(i, MAX_AGE);
        world.setBlockState(pos, state.with(AGE, i), Block.NOTIFY_LISTENERS);
    }

    static {
        AGE_TO_SHAPE = new VoxelShape[]{
                Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 6.0D, 12.0D),
                Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 7.0D, 12.0D),
                Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D),
                Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D)
        };
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "beamer";
    }
}

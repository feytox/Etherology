package ru.feytox.etherology.block.brewingCauldron;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.CorruptionBucket;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import static ru.feytox.etherology.registry.block.EBlocks.BREWING_CAULDRON_BLOCK_ENTITY;

public class BrewingCauldronBlock extends HorizontalFacingBlock implements RegistrableBlock, BlockEntityProvider {

    public static final IntProperty LEVEL = IntProperty.of("level", 0, 8);
    public static final IntProperty ASPECTS_LVL = IntProperty.of("aspects_lvl", 0, 100);

    private static final MapCodec<BrewingCauldronBlock> CODEC = MapCodec.unit(BrewingCauldronBlock::new);
    private static final VoxelShape RAYCAST_SHAPE;
    private static final VoxelShape OUTLINE_SHAPE;
    private static final VoxelShape INPUT_SHAPE;

    public BrewingCauldronBlock() {
        super(Settings.copy(Blocks.CAULDRON).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LEVEL, 0)
                .with(ASPECTS_LVL, 0));
    }

    public static boolean isFilled(BlockState state) {
        return state.get(LEVEL) > 0;
    }

    public static boolean isFilled(ServerWorld world, BlockPos pos) {
        return isFilled(world.getBlockState(pos));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL, ASPECTS_LVL);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && !moved) {
            if (world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron) {
                ItemScatterer.spawn(world, pos, cauldron);
            }
            super.onStateReplaced(state, world, pos, newState, false);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != BREWING_CAULDRON_BLOCK_ENTITY) return null;

        return world.isClient ? BrewingCauldronBlockEntity::clientTicker : BrewingCauldronBlockEntity::serverTicker;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack handStack = player.getStackInHand(hand);
        if (handStack.isOf(Items.BUCKET)) {
            return fillBucketWithCorruption(world, state, pos, player, handStack, hand);
        }

        if (!handStack.isOf(Items.WATER_BUCKET)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int waterLevel = state.get(LEVEL);
        if (waterLevel == 8) return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        if (world.isClient) return ItemActionResult.SUCCESS;
        fillCauldron(state, world, pos, player, hand, handStack);

        return ItemActionResult.CONSUME;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return player.isSneaking() ? tryTakeLastItem(world, player, state, pos) : mixWater(world, state, pos);
    }

    @NotNull
    private ItemActionResult fillBucketWithCorruption(World world, BlockState state, BlockPos pos, PlayerEntity player, ItemStack handStack, Hand hand) {
        if (world.isClient || state.get(LEVEL) == 0) return ItemActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

        boolean wasWithAspects = cauldron.isWasWithAspects();
        ItemStack filledStack = wasWithAspects ? CorruptionBucket.createBucketStack(cauldron.getAspects()) : Items.WATER_BUCKET.getDefaultStack();
        if (filledStack == null) return ItemActionResult.CONSUME;
        ItemStack newStack = ItemUsage.exchangeStack(handStack, player, filledStack);
        player.setStackInHand(hand, newStack);

        world.setBlockState(pos, state.with(LEVEL, 0).with(ASPECTS_LVL, 0));
        cauldron.clearAspects((ServerWorld) world);
        ItemScatterer.spawn(world, pos.up(), cauldron);
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ItemActionResult.CONSUME;
    }

    private ActionResult tryTakeLastItem(World world, PlayerEntity player, BlockState state, BlockPos pos) {
        if (world.isClient || !isFilled(state)) return ActionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return ActionResult.PASS;

        ItemStack cauldronStack = cauldron.takeLastStack((ServerWorld) world);
        if (cauldronStack.isEmpty()) return ActionResult.PASS;

        player.setStackInHand(Hand.MAIN_HAND, cauldronStack);
        return ActionResult.SUCCESS;
    }

    private ActionResult mixWater(World world, BlockState state, BlockPos pos) {
        if (!isFilled(state) || world.isClient) return ActionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return ActionResult.PASS;
        if (cauldron.getTemperature() < 100) return ActionResult.PASS;

        cauldron.mixWater(world);
        return ActionResult.SUCCESS;
    }

    private void fillCauldron(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack) {
        world.setBlockState(pos, state.with(LEVEL, 8));
        ItemStack outputStack = new ItemStack(Items.BUCKET);
        player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, outputStack));
        
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!isFilled(state)) return;
        if (!(entity instanceof ItemEntity itemEntity)) return;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return;
        if (cauldron.getTemperature() < 100) return;
        if (!VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), INPUT_SHAPE, BooleanBiFunction.AND)) return;

        cauldron.consumeItem((ServerWorld) world, itemEntity, state);
    }

    @Override
    public String getBlockId() {
        return "brewing_cauldron";
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
        return new BrewingCauldronBlockEntity(pos, state);
    }

    static {
        RAYCAST_SHAPE = createCuboidShape(3.5, 5.0, 3.5, 12.5, 15.0, 12.5);
        INPUT_SHAPE = createCuboidShape(3.5, 5.0, 3.5, 12.5, 8.0, 12.5);
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(1.5, 0, 1.5, 14.5, 15.0, 14.5),
                RAYCAST_SHAPE,
                BooleanBiFunction.ONLY_FIRST);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }
}

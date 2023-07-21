package ru.feytox.etherology.block.brewingCauldron;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import static ru.feytox.etherology.registry.block.EBlocks.BREWING_CAULDRON_BLOCK_ENTITY;

@SuppressWarnings("deprecation")
public class BrewingCauldronBlock extends HorizontalFacingBlock implements RegistrableBlock, BlockEntityProvider {

    public static final IntProperty LEVEL = IntProperty.of("level", 0, 8);
    public static final IntProperty ASPECTS_LVL = IntProperty.of("aspects_lvl", 0, 100);
    public static final IntProperty TEMPERATURE = IntProperty.of("temperature", 0, 100);

    private static final VoxelShape RAYCAST_SHAPE;
    private static final VoxelShape OUTLINE_SHAPE;
    private static final VoxelShape INPUT_SHAPE;

    public BrewingCauldronBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LEVEL, 0)
                .with(ASPECTS_LVL, 0)
                .with(TEMPERATURE, 20));
    }

    public static boolean isFilled(BlockState state) {
        return state.get(LEVEL) > 0;
    }

    public static boolean isFilled(ServerWorld world, BlockPos pos) {
        return isFilled(world.getBlockState(pos));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL, TEMPERATURE, ASPECTS_LVL);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != BREWING_CAULDRON_BLOCK_ENTITY) return null;

        return world.isClient ? BrewingCauldronBlockEntity::clientTicker : BrewingCauldronBlockEntity::serverTicker;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack handStack = player.getStackInHand(hand);
        if (handStack.isOf(Items.BUCKET)) {
            return fillBucketWithCorruption(world, state, pos, player, handStack, hand);
        }
        
        if (!handStack.isOf(Items.WATER_BUCKET)) {
            return player.isSneaking() ? tryTakeLastItem(world, player, hand, state, pos) :
                    mixWater(world, state, pos);
        }
        
        int waterLevel = state.get(LEVEL);
        if (waterLevel == 8) return ActionResult.PASS;
        if (world.isClient) return ActionResult.SUCCESS;
        fillCauldron(state, world, pos, player, hand, handStack);

        return ActionResult.CONSUME;
    }

    @NotNull
    private ActionResult fillBucketWithCorruption(World world, BlockState state, BlockPos pos, PlayerEntity player, ItemStack handStack, Hand hand) {
        if (world.isClient || state.get(LEVEL) == 0) return ActionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return ActionResult.PASS;

        boolean wasWithAspects = cauldron.isWasWithAspects();
        ItemStack filledStack = wasWithAspects ? CorruptionBucket.createBucketStack(cauldron.getAspects()) : Items.WATER_BUCKET.getDefaultStack();
        if (filledStack == null) return ActionResult.PASS;
        ItemStack newStack = ItemUsage.exchangeStack(handStack, player, filledStack);
        player.setStackInHand(hand, newStack);

        world.setBlockState(pos, state.with(LEVEL, 0).with(ASPECTS_LVL, 0));
        cauldron.clearAspects((ServerWorld) world);
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResult.SUCCESS;
    }

    private ActionResult tryTakeLastItem(World world, PlayerEntity player, Hand hand, BlockState state, BlockPos pos) {
        if (world.isClient || !isFilled(state)) return ActionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return ActionResult.PASS;

        ItemStack cauldronStack = cauldron.takeLastStack((ServerWorld) world);
        if (cauldronStack.isEmpty()) return ActionResult.PASS;

        player.setStackInHand(hand, cauldronStack);
        return ActionResult.SUCCESS;
    }

    private ActionResult mixWater(World world, BlockState state, BlockPos pos) {
        if (!isFilled(state) || world.isClient) return ActionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return ActionResult.PASS;

        cauldron.mixWater();
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
        if (state.get(TEMPERATURE) < 100) return;

        if (!(entity instanceof ItemEntity itemEntity)) return;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return;
        if (!VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), INPUT_SHAPE, BooleanBiFunction.AND)) return;

        cauldron.consumeItem((ServerWorld) world, itemEntity, state);
    }

    @Override
    public Block getBlockInstance() {
        return this;
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
        RAYCAST_SHAPE = createCuboidShape(3.5, 5.0, 3.5, 12.5, 15.1, 12.5);
        INPUT_SHAPE = createCuboidShape(3.5, 5.0, 3.5, 12.5, 8.0, 12.5);
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(1.5, 0, 1.5, 14.5, 15.1, 14.5),
                RAYCAST_SHAPE,
                BooleanBiFunction.ONLY_FIRST);
    }
}

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
import net.minecraft.sound.SoundEvent;
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
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import static ru.feytox.etherology.registry.block.EBlocks.BREWING_CAULDRON_BLOCK_ENTITY;

public class BrewingCauldronBlock extends HorizontalFacingBlock implements RegistrableBlock, BlockEntityProvider {

    public static final IntProperty LEVEL = IntProperty.of("level", 0, 8);
    public static final IntProperty TEMPERATURE = IntProperty.of("temperature", 0, 100);

    private static final VoxelShape RAYCAST_SHAPE;
    protected static final VoxelShape OUTLINE_SHAPE;

    public BrewingCauldronBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LEVEL, 0)
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
        builder.add(FACING, LEVEL, TEMPERATURE);
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
        if (!handStack.isOf(Items.WATER_BUCKET)) return ActionResult.PASS;
        int waterLevel = state.get(LEVEL);

        if (waterLevel == 8) return ActionResult.PASS;
        if (!world.isClient) {
            useBucket(state, world, pos, player, hand, handStack);
            // TODO: 01.07.2023 add water bucket with aspects
        }

        return ActionResult.success(world.isClient);
    }

    private void useBucket(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack) {
        world.setBlockState(pos, state.with(LEVEL, 8));
        ItemStack outputStack = new ItemStack(Items.BUCKET);
        player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, outputStack));

        GameEvent gameEvent = GameEvent.FLUID_PLACE;
        SoundEvent soundEvent = SoundEvents.ITEM_BUCKET_EMPTY;
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, gameEvent, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!isFilled(state)) return;
        if (state.get(TEMPERATURE) < 100) return;

        if (!(entity instanceof ItemEntity itemEntity)) return;
        if (!(world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron)) return;
        if (!VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), RAYCAST_SHAPE, BooleanBiFunction.AND)) return;

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
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(1.5, 0, 1.5, 14.5, 15.1, 14.5),
                RAYCAST_SHAPE,
                BooleanBiFunction.ONLY_FIRST);
    }
}

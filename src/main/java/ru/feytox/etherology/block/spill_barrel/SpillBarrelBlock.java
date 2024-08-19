package ru.feytox.etherology.block.spill_barrel;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import java.util.List;

public class SpillBarrelBlock extends Block implements RegistrableBlock, BlockEntityProvider {

    private static final BooleanProperty WITH_FRAME = BooleanProperty.of("with_frame");
    private static final VoxelShape NORTH_LOG;
    private static final VoxelShape SOUTH_LOG;
    private static final VoxelShape EAST_LOG;
    private static final VoxelShape WEST_LOG;
    private static final VoxelShape FRAME_SHAPE;
    private static final VoxelShape NORTH_BARREL;
    private static final VoxelShape EAST_BARREL;

    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape EAST_SHAPE;


    public SpillBarrelBlock() {
        super(Settings.copy(Blocks.BARREL).nonOpaque());
        this.setDefaultState(this.getDefaultState()
                .with(WITH_FRAME, false)
                .with(HorizontalFacingBlock.FACING, Direction.NORTH));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);

        ContainerComponent barrelData = stack.get(DataComponentTypes.CONTAINER);
        ItemStack potionStack = barrelData != null ? barrelData.copyFirstStack() : null;
        long potionCount = barrelData != null ? barrelData.streamNonEmpty().count() : 0;
        MutableText potionInfo = potionStack == null ? null : SpillBarrelBlockEntity.getPotionInfo(potionStack, potionCount, false, Text.empty());

        if (potionCount == 0 || potionInfo == null) {
            tooltip.add(1, Text.translatable("lore.etherology.spill_barrel.empty").formatted(Formatting.GRAY));
            return;
        }

        tooltip.add(1, potionInfo.formatted(Formatting.GRAY));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof SpillBarrelBlockEntity spillBarrel)) return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

        ItemStack handStack = player.getStackInHand(hand);
        if (handStack.isEmpty()) {
            spillBarrel.showPotionsInfo(player);
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        if (spillBarrel.tryFillBarrel(handStack.copy())) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, Items.GLASS_BOTTLE.getDefaultStack()));
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            return ItemActionResult.CONSUME;
        }

        if (!spillBarrel.isEmpty() && !handStack.isOf(Items.POTION)) {
            ItemStack outputStack = spillBarrel.tryEmptyBarrel(handStack);
            if (outputStack.isOf(Items.POTION)) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, outputStack));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
                return ItemActionResult.CONSUME;
            }
        }

        spillBarrel.showPotionsInfo(player);
        return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // TODO: 15/04/2023 add outline for frame
        return switch (state.get(HorizontalFacingBlock.FACING)) {
            case NORTH, SOUTH -> NORTH_SHAPE;
            case EAST, WEST -> EAST_SHAPE;
            default -> super.getOutlineShape(state, world, pos, context);
        };

    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpillBarrelBlockEntity spillBarrel) {
            if (!world.isClient && (!player.isCreative() || (player.isCreative() && !spillBarrel.isEmpty()))) {
                ItemStack barrelStack = asItem().getDefaultStack();
                barrelStack.applyComponentsFrom(spillBarrel.createComponentMap());
                if (spillBarrel.hasCustomName()) {
                    barrelStack.set(DataComponentTypes.CUSTOM_NAME, spillBarrel.getCustomName());
                }
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, barrelStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING, WITH_FRAME);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState underState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        BlockState state = this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        if (underState.isAir() || underState.isOf(EBlocks.SPILL_BARREL)) {
            state = state.with(WITH_FRAME, true);
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!neighborPos.equals(pos.down())) return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);

        if (neighborState.isAir() || neighborState.isOf(EBlocks.SPILL_BARREL)) {
            return state.with(WITH_FRAME, true);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        Text customName = itemStack.get(DataComponentTypes.CUSTOM_NAME);
        if (customName != null && blockEntity instanceof SpillBarrelBlockEntity spillBarrel) {
            spillBarrel.setCustomName(customName);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpillBarrelBlockEntity(pos, state);
    }

    @Override
    public String getBlockId() {
        return "spill_barrel";
    }

    static {
        NORTH_LOG = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 3.0D, 6.0D);
        SOUTH_LOG = Block.createCuboidShape(3.0D, 0.0D, 10.0D, 13.0D, 3.0D, 13.0D);
        EAST_LOG = Block.createCuboidShape(10.0D, 0.0D, 3.0D, 13.0D, 3.0D, 13.0D);
        WEST_LOG = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 6.0D, 3.0D, 13.0D);
        FRAME_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        NORTH_BARREL = Block.createCuboidShape(4.0D, 2.0D, 2.0D, 12.0D, 10.0D, 14.0D);
        EAST_BARREL = Block.createCuboidShape(2.0D, 2.0D, 4.0D, 14.0D, 10.0D, 12.0D);

        NORTH_SHAPE = VoxelShapes.combineAndSimplify(
                NORTH_BARREL,
                VoxelShapes.union(NORTH_LOG, SOUTH_LOG),
                BooleanBiFunction.OR
        );
        EAST_SHAPE = VoxelShapes.combineAndSimplify(
                EAST_BARREL,
                VoxelShapes.union(EAST_LOG, WEST_LOG),
                BooleanBiFunction.OR
        );
    }
}

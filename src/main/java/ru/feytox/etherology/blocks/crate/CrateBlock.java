package ru.feytox.etherology.blocks.crate;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.BlocksRegistry;
import ru.feytox.etherology.ItemsRegistry;
import ru.feytox.etherology.util.registry.RegistrableBlock;

public class CrateBlock extends FallingBlock implements RegistrableBlock, BlockEntityProvider, LandingBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty FALLING = BooleanProperty.of("falling");
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;


    public CrateBlock() {
        super(FabricBlockSettings.copyOf(Blocks.CHEST).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FALLING, false));
    }

    @Override
    public Item asItem() {
        return BlocksRegistry.CRATE.getItem();
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FALLING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return state.get(FALLING) ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH, SOUTH -> NORTH_SHAPE;
            case WEST, EAST -> WEST_SHAPE;
            default -> super.getOutlineShape(state, world, pos, context);
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.isSneaking() && player.getMainHandStack().isEmpty() && world.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
                ItemStack crateStack = ItemsRegistry.CARRIED_CRATE.getDefaultStack();
                crate.setStackNbt(crateStack);
                crate.clear();
                crate.markDirty();
                player.setStackInHand(Hand.MAIN_HAND, crateStack);
                world.removeBlock(pos, true);
                return ActionResult.SUCCESS;
            }

            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        world.setBlockState(pos, fallingBlockState.with(FALLING, false));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return BlocksRegistry.CRATE.getItem().getDefaultStack();
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (fallingBlockEntity.blockEntityData == null) return;
        DefaultedList<ItemStack> items = DefaultedList.ofSize(10, ItemStack.EMPTY);
        Inventories.readNbt(fallingBlockEntity.blockEntityData, items);
        ItemScatterer.spawn(world, pos, items);
        fallingBlockEntity.blockEntityData = null;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && !moved && !state.get(FALLING)) {
            dropInventory(world, pos);
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    private void dropInventory(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof CrateBlockEntity crate) {
            ItemScatterer.spawn(world, pos, crate);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(FALLING) || !FallingBlock.canFallThrough(world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) {
            return;
        }
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
        this.configureFallingBlockEntity(fallingBlockEntity);
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        if (!(entity.world.getBlockEntity(entity.getBlockPos()) instanceof CrateBlockEntity crate)) return;

        NbtCompound nbtCompound = crate.createNbt();
        crate.readNbt(nbtCompound);
        entity.blockEntityData = nbtCompound;
        crate.clear();
        crate.markDirty();
        entity.dropItem = true;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean isCarried = ctx.getStack().isOf(ItemsRegistry.CARRIED_CRATE);
        boolean isInAir = ctx.getWorld().isAir(ctx.getBlockPos().down());
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(FALLING, isCarried && isInAir);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "crate";
    }

    static {
        NORTH_SHAPE = createCuboidShape(1, 0, 3, 15, 10, 13);
        WEST_SHAPE = createCuboidShape(3, 0, 1, 13, 10, 15);
    }
}

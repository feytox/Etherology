package ru.feytox.etherology.block.furniture;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.FurnitureType;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import java.util.Optional;

import static ru.feytox.etherology.enums.FurnitureType.EMPTY;
import static ru.feytox.etherology.registry.block.EBlocks.FURNITURE_BLOCK_ENTITY;

public abstract class AbstractFurSlabBlock extends Block implements RegistrableBlock, BlockEntityProvider {

    public static final BooleanProperty BOTTOM_ACTIVE = BooleanProperty.of("bottom_active");
    public static final BooleanProperty TOP_ACTIVE = BooleanProperty.of("top_active");
    public static final EnumProperty<FurnitureType> TOP_TYPE = EnumProperty.of("top_type", FurnitureType.class);
    public static final EnumProperty<FurnitureType> BOTTOM_TYPE = EnumProperty.of("bottom_type", FurnitureType.class);
    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

    private final String blockId;
    private final FurnitureType furType;

    public AbstractFurSlabBlock(String blockId, Settings settings, FurnitureType furType) {
        super(settings);
        this.blockId = blockId;
        this.furType = furType;
        setDefaultState(getDefaultState()
                .with(BOTTOM_TYPE, furType)
                .with(TOP_TYPE, EMPTY)
                .with(BOTTOM_ACTIVE, false)
                .with(TOP_ACTIVE, false)
                .with(HorizontalFacingBlock.FACING, Direction.NORTH));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof FurSlabBlockEntity furBlockEntity) {
            Optional<Vec2f> match = getHitPos(hit, state.get(HorizontalFacingBlock.FACING));
            if (match.isEmpty()) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

            Vec2f hitPos = match.get();
            if (hitPos.y >= 0.5f) {
                furBlockEntity.topUse(world, state, player, hitPos, hand);
            } else {
                furBlockEntity.bottomUse(world, state, player, hitPos, hand);
            }
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TOP_TYPE, BOTTOM_TYPE, TOP_ACTIVE, BOTTOM_ACTIVE, HorizontalFacingBlock.FACING);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return super.onBreak(world, pos, state.with(BOTTOM_TYPE, EMPTY), player);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(newState.getBlock() instanceof AbstractFurSlabBlock)) {
            if (be instanceof FurSlabBlockEntity furniture) {
                ImplementedInventory bottomInv = furniture.getInventory(false);
                ImplementedInventory topInv = furniture.getInventory(true);
                if (bottomInv != null) ItemScatterer.spawn(world, pos, bottomInv);
                if (topInv != null) ItemScatterer.spawn(world, pos, topInv);
            }
            world.removeBlockEntity(pos);
        } else {
            if (be instanceof FurSlabBlockEntity furniture) {
                furniture.onUpdateState(newState);
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        BlockState state = world.getBlockState(blockPos);

        if (state.getBlock() instanceof AbstractFurSlabBlock && !isFull(state)) {
            FurnitureType topType = state.get(TOP_TYPE);
            BlockState newState = state.with(BOTTOM_TYPE, this.furType);
            if (topType.isEmpty()) newState = state.with(TOP_TYPE, this.furType);

            if (world.getBlockEntity(blockPos, FURNITURE_BLOCK_ENTITY).isEmpty()) {
                BlockEntity newBlockEntity = createBlockEntity(blockPos, newState);
                if (newBlockEntity != null) world.addBlockEntity(newBlockEntity);
            }
            return newState;
        }

        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        BlockState bottomState = getDefaultState().with(BOTTOM_TYPE, this.furType)
                .with(TOP_TYPE, EMPTY).with(HorizontalFacingBlock.FACING, facing);
        BlockState topState = getDefaultState().with(BOTTOM_TYPE, EMPTY)
                .with(TOP_TYPE, this.furType).with(HorizontalFacingBlock.FACING, facing);
        Direction direction = ctx.getSide();
        return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5)) ? bottomState : topState;
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        Item item = itemStack.getItem();
        if (isFull(state) || !(item instanceof BlockItem blockItem)) return false;

        Block block = blockItem.getBlock();
        if (!(block instanceof AbstractFurSlabBlock)) return false;

        if (!context.canReplaceExisting()) return true;

        boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5;
        Direction direction = context.getSide();
        FurnitureType topType = state.get(TOP_TYPE);
        if (topType.isEmpty()) {
            return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
        }
        return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
    }

    public boolean isFull(BlockState state) {
        FurnitureType topType = state.get(TOP_TYPE);
        FurnitureType bottomType = state.get(BOTTOM_TYPE);
        return !topType.isEmpty() && !bottomType.isEmpty();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        FurnitureType bottomType = state.get(BOTTOM_TYPE);
        FurnitureType topType = state.get(TOP_TYPE);

        return new FurSlabBlockEntity(pos, state, bottomType, topType);
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        FurnitureType bottomType = state.get(BOTTOM_TYPE);
        FurnitureType topType = state.get(TOP_TYPE);

        return bottomType.isSidedTransparent() || topType.isSidedTransparent();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (isFull(state)) return VoxelShapes.fullCube();

        FurnitureType topType = state.get(TOP_TYPE);
        if (topType.isEmpty()) return BOTTOM_SHAPE;
        else return TOP_SHAPE;
    }

    @Override
    public String getBlockId() {
        return blockId;
    }

    /**
     * code from Vanilla Chiseled Bookshelf
     * @see ChiseledBookshelfBlock
     */
    public static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getSide();
        if (facing != direction) {
            return Optional.empty();
        } else {
            BlockPos blockPos = hit.getBlockPos().offset(direction);
            Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            double d = vec3d.getX();
            double e = vec3d.getY();
            double f = vec3d.getZ();

            return switch (direction) {
                case NORTH -> Optional.of(new Vec2f((float) (1.0 - d), (float) e));
                case SOUTH -> Optional.of(new Vec2f((float) d, (float) e));
                case WEST -> Optional.of(new Vec2f((float) f, (float) e));
                case EAST -> Optional.of(new Vec2f((float) (1.0 - f), (float) e));
                case DOWN, UP -> Optional.empty();
            };
        }
    }
}

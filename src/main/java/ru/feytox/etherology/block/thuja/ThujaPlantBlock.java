package ru.feytox.etherology.block.thuja;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import java.util.Optional;

import static net.minecraft.block.AbstractPlantStemBlock.AGE;
import static net.minecraft.block.AbstractPlantStemBlock.MAX_AGE;

public class ThujaPlantBlock extends AbstractPlantBlock implements RegistrableBlock, ThujaShapeController {

    private static final MapCodec<ThujaPlantBlock> CODEC = MapCodec.unit(ThujaPlantBlock::new);

    public ThujaPlantBlock() {
        super(Settings.create().mapColor(MapColor.EMERALD_GREEN).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS), Direction.UP, ThujaBlock.OUTLINE_SHAPE, false);
        setDefaultState(getDefaultState()
                .with(ThujaBlock.SHAPE, ThujaShape.BUSH)
        );
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemActionResult result = useShears(world, state, pos, player, stack, hand);
        return result != null ? result : super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Nullable
    private ItemActionResult useShears(World world, BlockState plantState, BlockPos plantPos, PlayerEntity player, ItemStack stack, Hand hand) {
        if (world.isClient) return null;
        if (!(stack.getItem() instanceof ShearsItem)) return null;

        Optional<BlockPos> optional = getCrownPos(world, plantState, plantPos);
        if (optional.isEmpty()) return null;

        BlockPos pos = optional.get();
        BlockState state = world.getBlockState(pos);
        if (state.get(AGE) == MAX_AGE) return null;

        world.setBlockState(pos, state.with(AGE, MAX_AGE), NOTIFY_LISTENERS);
        stack.damage(1, player, LivingEntity.getSlotForHand(hand));
        world.playSound(null, plantPos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return ItemActionResult.SUCCESS;
    }

    private Optional<BlockPos> getCrownPos(World world, BlockState plantState, BlockPos plantPos) {
        return BlockLocating.findColumnEnd(world, plantPos, plantState.getBlock(), growthDirection, getStem());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ThujaBlock.SHAPE);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        return state == null ? null : getThujaPlacementState(state, ctx);
    }

    @Override
    protected MapCodec<? extends AbstractPlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        state = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        return getThujaStateForNeighborUpdate(state, world, pos, neighborPos);
    }

    @Override
    protected boolean canAttachTo(BlockState state) {
        return state.isIn(BlockTags.DIRT) || state.getBlock() instanceof ThujaShapeController;
    }

    @Override
    protected AbstractPlantStemBlock getStem() {
        return DecoBlocks.THUJA;
    }

    @Override
    public String getBlockId() {
        return "thuja_plant";
    }
}

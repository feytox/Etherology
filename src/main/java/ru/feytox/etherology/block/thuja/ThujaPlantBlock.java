package ru.feytox.etherology.block.thuja;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
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

    public ThujaPlantBlock() {
        super(AbstractBlock.Settings.of(Material.PLANT, MapColor.EMERALD_GREEN).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS), Direction.UP, ThujaBlock.OUTLINE_SHAPE, false);
        setDefaultState(getDefaultState()
                .with(ThujaBlock.SHAPE, ThujaShape.BUSH)
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = useShears(world, state, pos, player, hand);
        return result != null ? result : super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    private ActionResult useShears(World world, BlockState plantState, BlockPos plantPos, PlayerEntity player, Hand hand) {
        if (world.isClient) return null;

        ItemStack handStack = player.getStackInHand(hand);
        if (!(handStack.getItem() instanceof ShearsItem)) return null;

        Optional<BlockPos> optional = getCrownPos(world, plantState, plantPos);
        if (optional.isEmpty()) return null;

        BlockPos pos = optional.get();
        BlockState state = world.getBlockState(pos);
        if (state.get(AGE) == MAX_AGE) return null;

        world.setBlockState(pos, state.with(AGE, MAX_AGE), NOTIFY_LISTENERS);
        handStack.damage(1, player, playerX -> playerX.sendToolBreakStatus(hand));
        world.playSound(null, plantPos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return ActionResult.SUCCESS;
    }

    private Optional<BlockPos> getCrownPos(World world, BlockState plantState, BlockPos plantPos) {
        return BlockLocating.findColumnEnd(world, plantPos, plantState.getBlock(), growthDirection, getStem());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ThujaBlock.SHAPE);
    }

    protected int getGrowthAmount(Random random) {
        return MathHelper.nextInt(random, 0, 18);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Optional<BlockPos> optional = getCrownPos(world, state, pos);
        if (optional.isEmpty()) return;
        BlockPos crownPos = optional.get();
        BlockState crownState = world.getBlockState(crownPos);

        int i = crownState.get(AGE) + getGrowthAmount(random);
        i = Math.min(i, MAX_AGE);
        world.setBlockState(crownPos, crownState.with(AGE, i), Block.NOTIFY_LISTENERS);
        if (i == MAX_AGE) super.grow(world, random, pos, state);
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

    @Override
    protected AbstractPlantStemBlock getStem() {
        return DecoBlocks.THUJA;
    }

    @Override
    public String getBlockId() {
        return "thuja_plant";
    }
}

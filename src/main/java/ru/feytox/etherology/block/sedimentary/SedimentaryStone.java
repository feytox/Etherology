package ru.feytox.etherology.block.sedimentary;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK_ENTITY;

public class SedimentaryStone extends Block implements RegistrableBlock, BlockEntityProvider {

    public static final EnumProperty<EssenceZoneType> ESSENCE_STATE = EnumProperty.of("essence_state", EssenceZoneType.class);
    public static final IntProperty ESSENCE_LEVEL = IntProperty.of("essence_level", 0, 4);

    public SedimentaryStone() {
        super(Settings.copy(Blocks.STONE));
        setDefaultState(getDefaultState().with(ESSENCE_STATE, EssenceZoneType.EMPTY).with(ESSENCE_LEVEL, 0));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof AxeItem)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;


        boolean result = false;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SedimentaryStoneBlockEntity sedimentaryBlock) {
            result = sedimentaryBlock.onUseAxe(state, world, hit.getSide().getVector());
        }
        return result ? ItemActionResult.SUCCESS : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SedimentaryStoneBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ESSENCE_STATE, ESSENCE_LEVEL);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return SedimentaryStoneBlockEntity.getTicker(world, type, SEDIMENTARY_BLOCK_ENTITY);
    }

    @Override
    public String getBlockId() {
        return "sedimentary_stone";
    }
}

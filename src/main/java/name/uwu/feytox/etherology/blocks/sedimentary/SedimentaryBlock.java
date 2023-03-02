package name.uwu.feytox.etherology.blocks.sedimentary;

import name.uwu.feytox.etherology.enums.SedimentaryStates;
import name.uwu.feytox.etherology.util.registry.SimpleBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.SEDIMENTARY_BLOCK_ENTITY;
import static name.uwu.feytox.etherology.enums.SedimentaryStates.EMPTY;

public class SedimentaryBlock extends SimpleBlock implements BlockEntityProvider {
    public static final EnumProperty<SedimentaryStates> ESSENCE_STATE = EnumProperty.of("essence_state", SedimentaryStates.class);
    public static final IntProperty ESSENCE_LEVEL = IntProperty.of("essence_level", 0, 4);

    public SedimentaryBlock() {
        super("sedimentary_block", FabricBlockSettings.of(Material.STONE));
        setDefaultState(getDefaultState().with(ESSENCE_STATE, EMPTY).with(ESSENCE_LEVEL, 0));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean iterResult = false;
        for (ItemStack itemStack : player.getHandItems()) {
            if (itemStack.getItem() instanceof AxeItem) {
                iterResult = true;
                break;
            }
        }
        if (!iterResult) return super.onUse(state, world, pos, player, hand, hit);

        boolean result = false;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SedimentaryBlockEntity sedimentaryBlock) {
            result = sedimentaryBlock.onUseAxe(state, world, player);
        }
        return result ? ActionResult.SUCCESS : super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SedimentaryBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ESSENCE_STATE, ESSENCE_LEVEL);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != SEDIMENTARY_BLOCK_ENTITY) return null;

        return world.isClient ? SedimentaryBlockEntity::clientTick : SedimentaryBlockEntity::serverTick;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
//        world.updateComparators(pos,this);
    }
}

package name.uwu.feytox.etherology.blocks.etherealStorage;

import name.uwu.feytox.etherology.util.registry.RegistrableBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.ETHEREAL_STORAGE_BLOCK_ENTITY;

public class EtherealStorageBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    private static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public EtherealStorageBlock() {
        super(FabricBlockSettings.of(Material.METAL));
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public String getBlockId() {
        return "ethereal_storage";
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealStorageBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ETHEREAL_STORAGE_BLOCK_ENTITY) return null;

        return world.isClient ? null : EtherealStorageBlockEntity::serverTicker;
    }
}

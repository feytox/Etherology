package ru.feytox.etherology.block.zone_blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.deprecated.SimpleBlock;

import static ru.feytox.etherology.registry.block.EBlocks.ZONE_CORE_BLOCK_ENTITY;

public class ZoneCoreBlock extends SimpleBlock implements BlockEntityProvider {
    public ZoneCoreBlock() {
        super("zone_core_block", FabricBlockSettings.of(Material.AIR).noCollision().dropsNothing().air()
                .blockVision((a, b, c) -> false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZoneCoreBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ZONE_CORE_BLOCK_ENTITY) return null;

        return world.isClient ? ZoneCoreBlockEntity::clientTick : ZoneCoreBlockEntity::serverTick;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.isClient) {
            super.onStateReplaced(state, world, pos, newState, moved);
            world.updateComparators(pos,this);
            return;
        }

        if (!state.isOf(newState.getBlock()) && !newState.isAir()) {
            BlockPos newPos = pos.add(0, 1, 0);
            while (!world.getBlockState(newPos).isAir()) {
                newPos = newPos.add(0, 1, 0);
            }
            world.setBlockState(newPos, state);
            ZoneCoreBlockEntity oldBlock = (ZoneCoreBlockEntity) world.getBlockEntity(pos);
            ZoneCoreBlockEntity newBlock = (ZoneCoreBlockEntity) world.getBlockEntity(newPos);
            if (oldBlock != null && newBlock != null) newBlock.copy(oldBlock);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
        world.updateComparators(pos,this);
    }
}

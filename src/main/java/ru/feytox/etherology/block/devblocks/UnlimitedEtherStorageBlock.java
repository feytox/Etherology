package ru.feytox.etherology.block.devblocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import static ru.feytox.etherology.registry.block.DevBlocks.UNLIMITED_ETHER_STORAGE_BLOCK_ENTITY_TYPE;

public class UnlimitedEtherStorageBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    public UnlimitedEtherStorageBlock() {
        super(FabricBlockSettings.of(Material.METAL));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != UNLIMITED_ETHER_STORAGE_BLOCK_ENTITY_TYPE) return null;

        return world.isClient ? null : UnlimitedEtherStorageBlockEntity::serverTicker;
    }

    @Override
    public String getBlockId() {
        return "unlimited_ether_storage";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UnlimitedEtherStorageBlockEntity(pos, state);
    }
}

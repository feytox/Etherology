package ru.feytox.etherology.block.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.registry.block.DecoBlocks;

public class EtherWallSignBlock extends WallSignBlock {

    public EtherWallSignBlock(WoodType woodType, Settings settings) {
        super(woodType, settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignBlockEntity(DecoBlocks.ETHEROLOGY_SIGN, pos, state);
    }
}

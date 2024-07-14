package ru.feytox.etherology.block.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class EtherWallHangingSignBlock extends WallHangingSignBlock {

    public EtherWallHangingSignBlock(WoodType woodType, Settings settings) {
        super(woodType, settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherHangingSignBlockEntity(pos, state);
    }
}

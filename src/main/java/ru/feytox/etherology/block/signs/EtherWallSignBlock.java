package ru.feytox.etherology.block.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;

public class EtherWallSignBlock extends WallSignBlock {
    public EtherWallSignBlock(Settings settings, SignType signType) {
        super(settings, signType);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherSignBlockEntity(pos, state);
    }
}

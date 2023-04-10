package ru.feytox.etherology.blocks.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;

public class EtherSignBlock extends SignBlock {
    public EtherSignBlock(Settings settings, SignType signType) {
        super(settings, signType);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherSignBlockEntity(pos, state);
    }
}

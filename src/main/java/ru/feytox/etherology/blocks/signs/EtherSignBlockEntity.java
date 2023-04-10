package ru.feytox.etherology.blocks.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;

import static ru.feytox.etherology.DecoBlocks.ETHEROLOGY_SIGN;

public class EtherSignBlockEntity extends SignBlockEntity {
    public EtherSignBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEROLOGY_SIGN, pos, state);
    }
}

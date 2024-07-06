package ru.feytox.etherology.block.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;

import static ru.feytox.etherology.registry.block.DecoBlocks.ETHEROLOGY_SIGN;

@Deprecated
public class EtherSignBlockEntity extends SignBlockEntity {
    public EtherSignBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEROLOGY_SIGN, pos, state);
    }
}

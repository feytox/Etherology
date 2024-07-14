package ru.feytox.etherology.block.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.registry.block.DecoBlocks;

@Deprecated
public class EtherSignBlockEntity extends SignBlockEntity {

    public EtherSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(DecoBlocks.ETHEROLOGY_SIGN, blockPos, blockState);
    }
}

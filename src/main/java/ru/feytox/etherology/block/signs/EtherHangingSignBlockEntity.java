package ru.feytox.etherology.block.signs;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.registry.block.DecoBlocks;

/**
 * @see HangingSignBlockEntity
 */
public class EtherHangingSignBlockEntity extends SignBlockEntity {

    public EtherHangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(DecoBlocks.ETHEROLOGY_HANGING_SIGN, blockPos, blockState);
    }

    public int getTextLineHeight() {
        return 9;
    }

    public int getMaxTextWidth() {
        return 60;
    }

    public SoundEvent getInteractionFailSound() {
        return SoundEvents.BLOCK_HANGING_SIGN_WAXED_INTERACT_FAIL;
    }
}

package ru.feytox.etherology.block.generators.spinner;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.generators.AbstractGenerator;

import static ru.feytox.etherology.registry.block.EBlocks.SPINNER_BLOCK_ENTITY;

public class SpinnerBlock extends AbstractGenerator {

    private static final MapCodec<SpinnerBlock> CODEC = MapCodec.unit(SpinnerBlock::new);

    public SpinnerBlock() {
        super(Settings.copy(Blocks.IRON_BLOCK),
                "spinner", 30*20, 120*20, 0.2f);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return SPINNER_BLOCK_ENTITY;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpinnerBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends FacingBlock> getCodec() {
        return CODEC;
    }
}

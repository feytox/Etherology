package ru.feytox.etherology.block.generators.metronome;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.generators.AbstractGenerator;

import static ru.feytox.etherology.registry.block.EBlocks.METRONOME_BLOCK_ENTITY;

public class MetronomeBlock extends AbstractGenerator {

    private static final MapCodec<MetronomeBlock> CODEC = MapCodec.unit(MetronomeBlock::new);

    public MetronomeBlock() {
        super(Settings.copy(Blocks.GOLD_BLOCK),
                "metronome", 20*20, 60*20, 0.1f);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return METRONOME_BLOCK_ENTITY;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MetronomeBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends FacingBlock> getCodec() {
        return CODEC;
    }
}

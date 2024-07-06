package ru.feytox.etherology.block.etherealGenerators.metronome;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.etherealGenerators.AbstractEtherealGenerator;

import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_METRONOME_BLOCK_ENTITY;

public class EtherealMetronomeBlock extends AbstractEtherealGenerator {

    private static final MapCodec<EtherealMetronomeBlock> CODEC = MapCodec.unit(EtherealMetronomeBlock::new);

    public EtherealMetronomeBlock() {
        super(Settings.copy(Blocks.GOLD_BLOCK),
                "ethereal_metronome", 20*20, 60*20, 0.1f);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return ETHEREAL_METRONOME_BLOCK_ENTITY;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealMetronomeBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends FacingBlock> getCodec() {
        return CODEC;
    }
}

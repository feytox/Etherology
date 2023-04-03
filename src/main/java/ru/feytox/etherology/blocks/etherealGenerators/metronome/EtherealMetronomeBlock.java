package ru.feytox.etherology.blocks.etherealGenerators.metronome;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.blocks.etherealGenerators.AbstractEtherealGenerator;

import static ru.feytox.etherology.BlocksRegistry.ETHEREAL_METRONOME_BLOCK_ENTITY;

public class EtherealMetronomeBlock extends AbstractEtherealGenerator {
    public EtherealMetronomeBlock() {
        super(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK),
                "ethereal_metronome", 20*20, 60*20, 0.05f);
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
}

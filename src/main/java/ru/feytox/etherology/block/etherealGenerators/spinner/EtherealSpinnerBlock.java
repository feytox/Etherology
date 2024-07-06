package ru.feytox.etherology.block.etherealGenerators.spinner;

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

import java.util.Map;

import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_SPINNER_BLOCK_ENTITY;

public class EtherealSpinnerBlock extends AbstractEtherealGenerator {

    private static final MapCodec<EtherealSpinnerBlock> CODEC = MapCodec.unit(EtherealSpinnerBlock::new);

    public EtherealSpinnerBlock() {
        super(Settings.copy(Blocks.IRON_BLOCK),
                "ethereal_spinner", 30*20, 120*20, 0.2f);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return ETHEREAL_SPINNER_BLOCK_ENTITY;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealSpinnerBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends FacingBlock> getCodec() {
        return CODEC;
    }
}

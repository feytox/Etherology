package ru.feytox.etherology.block.etherealGenerators.spinner;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.etherealGenerators.AbstractEtherealGenerator;

import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_SPINNER_BLOCK_ENTITY;

public class EtherealSpinnerBlock extends AbstractEtherealGenerator {

    public EtherealSpinnerBlock() {
        super(FabricBlockSettings.copy(Blocks.IRON_BLOCK),
                "ethereal_spinner", 30*20, 120*20, 0.1f);
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
}

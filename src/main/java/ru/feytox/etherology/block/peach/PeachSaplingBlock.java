package ru.feytox.etherology.block.peach;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SaplingGenerator;
import ru.feytox.etherology.util.misc.RegistrableBlock;
import ru.feytox.etherology.world.ConfiguredFeaturesGen;

import java.util.Optional;

public class PeachSaplingBlock extends SaplingBlock implements RegistrableBlock {

    private static final SaplingGenerator PEACH_GENERATOR = new SaplingGenerator("etherology:peach", Optional.empty(), Optional.of(ConfiguredFeaturesGen.PEACH_SAPLING_TREE), Optional.empty());

    public PeachSaplingBlock() {
        super(PEACH_GENERATOR, Settings.copy(Blocks.ACACIA_SAPLING));
    }

    @Override
    public String getBlockId() {
        return "peach_sapling";
    }
}

package ru.feytox.etherology.blocks.peachSapling;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import ru.feytox.etherology.util.registry.RegistrableBlock;

public class PeachSaplingBlock extends SaplingBlock implements RegistrableBlock {
    public PeachSaplingBlock() {
        super(new PeachSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.ACACIA_SAPLING));
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "peach_sapling";
    }
}

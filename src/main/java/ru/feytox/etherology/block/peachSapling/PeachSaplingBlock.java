package ru.feytox.etherology.block.peachSapling;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

public class PeachSaplingBlock extends SaplingBlock implements RegistrableBlock {
    public PeachSaplingBlock() {
        super(new PeachSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.ACACIA_SAPLING));
    }

    @Override
    public String getBlockId() {
        return "peach_sapling";
    }
}

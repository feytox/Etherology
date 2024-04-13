package ru.feytox.etherology.block.etherealChannel;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.misc.RegistrableBlock;

public class EtherealChannelCase extends Block implements RegistrableBlock {

    public EtherealChannelCase() {
        super(AbstractBlock.Settings.copy(DecoBlocks.PEACH_PLANKS));
    }

    @Override
    public String getBlockId() {
        return "ethereal_channel_case";
    }
}

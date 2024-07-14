package ru.feytox.etherology.block.etherealChannel;

import net.minecraft.block.Block;
import ru.feytox.etherology.registry.block.ExtraBlocksRegistry;
import ru.feytox.etherology.util.misc.RegistrableBlock;

public class EtherealChannelCase extends Block implements RegistrableBlock {

    public EtherealChannelCase() {
        super(Settings.copy(ExtraBlocksRegistry.PEACH_PLANKS));
    }

    @Override
    public String getBlockId() {
        return "ethereal_channel_case";
    }
}

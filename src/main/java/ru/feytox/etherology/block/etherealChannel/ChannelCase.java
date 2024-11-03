package ru.feytox.etherology.block.etherealChannel;

import net.minecraft.block.Block;
import ru.feytox.etherology.registry.block.ExtraBlocksRegistry;
import ru.feytox.etherology.util.misc.RegistrableBlock;

public class ChannelCase extends Block implements RegistrableBlock {

    public ChannelCase() {
        super(Settings.copy(ExtraBlocksRegistry.PEACH_PLANKS));
    }

    @Override
    public String getBlockId() {
        return "channel_case";
    }
}

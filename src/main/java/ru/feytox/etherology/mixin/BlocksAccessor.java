package ru.feytox.etherology.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Blocks.class)
public interface BlocksAccessor {
    @Invoker
    static Block callCreateOldStairsBlock(Block block) {
        throw new UnsupportedOperationException();
    }
}

package ru.feytox.etherology.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.registry.block.DecoBlocks;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "getStrippedState", at = @At("HEAD"), cancellable = true)
    private void onGetStrippedState(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        for (Block original : DecoBlocks.ETHER_LOGS.keySet()) {
            if (state.getBlock().equals(original)) {
                Block strippedBlock = DecoBlocks.ETHER_LOGS.get(original);
                cir.setReturnValue(Optional.of(strippedBlock.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS))));
            }
        }
    }
}

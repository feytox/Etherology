package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.block.crate.CrateBlock;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Shadow public abstract void onDestroyedOnLanding(Block block, BlockPos pos);

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;"))
    private void injectCrateFallingBlock(CallbackInfo ci, @Local Block block) {
        if (!(block instanceof CrateBlock)) return;

        FallingBlockEntity it = ((FallingBlockEntity)(Object) this);
        BlockPos blockPos = it.getBlockPos();

        if (it.timeFalling > 100 && (blockPos.getY() <= it.getWorld().getBottomY() || blockPos.getY() > it.getWorld().getTopY()) || it.timeFalling > 600) {
            onDestroyedOnLanding(block, blockPos);
        }
    }
}

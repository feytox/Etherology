package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.val;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RedstoneView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.magic.lens.RedstoneLensEffects;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin {

    // TODO: 13.12.2023 simplify redstone lens injects

    @ModifyReturnValue(method = "isReceivingRedstonePower", at = @At("RETURN"))
    private boolean injectRedstoneLens(boolean original, @Local(argsOnly = true) BlockPos pos) {
        RedstoneView world = (RedstoneView) this;
        if (!(world instanceof ServerWorld serverWorld)) return original;
        RedstoneLensEffects effects = RedstoneLensEffects.getServerState(serverWorld);
        val effect = effects.getUsage(pos);

        if (effect == null) return original;
        return original || effect.getPower() > 0;
    }

    @ModifyReturnValue(method = "getReceivedRedstonePower", at = @At("RETURN"))
    private int injectRedstoneLens2(int original, @Local(argsOnly = true) BlockPos pos) {
        RedstoneView world = (RedstoneView) this;
        if (!(world instanceof ServerWorld serverWorld)) return original;
        RedstoneLensEffects effects = RedstoneLensEffects.getServerState(serverWorld);
        val effect = effects.getUsage(pos);

        if (effect == null) return original;
        return Math.max(original, effect.getPower());
    }
}

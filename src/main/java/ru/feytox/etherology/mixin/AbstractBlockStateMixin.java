package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.val;
import net.minecraft.block.AbstractBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.magic.lense.RedstoneLensEffects;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @ModifyReturnValue(method = "getWeakRedstonePower", at = @At("RETURN"))
    public int injectRedstoneLens(int original, @Local BlockView blockView, @Local BlockPos pos) {
        if (!(blockView instanceof ServerWorld world)) return original;
        RedstoneLensEffects effects = RedstoneLensEffects.getServerState(world);
        val effect = effects.getUsage(pos);

        if (effect == null) return original;
        return Math.max(original, effect.getPower());
    }
}

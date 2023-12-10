package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.val;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.joml.Math;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.magic.lense.RedstoneLensEffects;

@Debug(export = true)
@Mixin(value = RedstoneWireBlock.class, priority = 2001) // apply after lithium
public class RedstoneWireBlockMixin {

    @ModifyReturnValue(method = "getStrongRedstonePower", at = @At("RETURN"))
    private int injectRedstoneLens(int original, @Local BlockView blockView, @Local BlockPos pos) {
        if (!(blockView instanceof ServerWorld world)) return original;
        RedstoneLensEffects effects = RedstoneLensEffects.getServerState(world);
        val effect = effects.getUsage(pos);

        if (effect == null) return original;
        return Math.max(original, effect.getPower());
    }

    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;getReceivedRedstonePower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I"))
    private int injectRedstoneLens2(int original, @Local World world, @Local BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return original;
        RedstoneLensEffects effects = RedstoneLensEffects.getServerState(serverWorld);
        val effect = effects.getUsage(pos);

        if (effect == null) return original;
        return Math.max(original, effect.getPower());
    }
}

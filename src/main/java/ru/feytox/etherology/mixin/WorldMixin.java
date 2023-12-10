package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.val;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.magic.lense.RedstoneLensEffects;

@Mixin(World.class)
public class WorldMixin {

    @ModifyReturnValue(method = "isReceivingRedstonePower", at = @At("RETURN"))
    private boolean injectRedstoneLens(boolean original, @Local BlockPos pos) {
        World world = (World)(Object) this;
        if (!(world instanceof ServerWorld serverWorld)) return original;
        RedstoneLensEffects effects = RedstoneLensEffects.getServerState(serverWorld);
        val effect = effects.getUsage(pos);

        if (effect == null) return original;
        return original || effect.getPower() > 0;
    }
}

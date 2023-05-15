package ru.feytox.etherology.mixin;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = KeyframeAnimation.class, remap = false)
public interface KeyframeAnimationAccessor {
    @Mutable
    @Accessor
    void setStopTick(int stopTick);
}

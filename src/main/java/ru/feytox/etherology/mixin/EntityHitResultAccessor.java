package ru.feytox.etherology.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityHitResult.class)
public interface EntityHitResultAccessor {
    @Mutable
    @Accessor
    void setEntity(Entity entity);
}

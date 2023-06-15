package ru.feytox.etherology.mixin;

import net.minecraft.data.client.Model;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(Model.class)
public interface ModelAccessor {
    @Accessor
    Optional<Identifier> getParent();
}

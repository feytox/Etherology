package ru.feytox.etherology.client.mixin;

import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BooleanSupplier;

@Mixin(StickyKeyBinding.class)
public interface StickyKeyBindingAccessor {
    @Accessor
    BooleanSupplier getToggleGetter();
}

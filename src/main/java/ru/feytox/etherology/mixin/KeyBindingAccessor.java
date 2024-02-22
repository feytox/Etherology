package ru.feytox.etherology.mixin;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor
    static Map<String, KeyBinding> getKEYS_BY_ID() {
        throw new UnsupportedOperationException();
    }
}

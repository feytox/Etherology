package ru.feytox.etherology.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {

    // stopship: continue fixing mixins
    // good luck

    @Invoker
    void callRenderOverlay(Identifier texture, float opacity);

    @Invoker
    void callRenderOverlay(DrawContext context, Identifier texture, float opacity);
}

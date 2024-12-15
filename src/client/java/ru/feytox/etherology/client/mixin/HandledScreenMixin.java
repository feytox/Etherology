package ru.feytox.etherology.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.client.gui.oculus.AspectTooltipComponent;
import ru.feytox.etherology.data.aspects.AspectsLoader;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.magic.aspects.AspectContainer;

import java.util.ArrayList;
import java.util.List;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    // TODO: 12.02.2024 cache

    @Shadow @Nullable protected Slot focusedSlot;

    @SuppressWarnings("rawtypes")
    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void drawOculusTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        HandledScreen it = ((HandledScreen) (Object) this);
        ItemStack cursorStack = it.getScreenHandler().getCursorStack();
        if (focusedSlot == null || !focusedSlot.hasStack() || !(cursorStack.getItem() instanceof OculusItem)) return;

        ItemStack focusedStack = focusedSlot.getStack();
        AspectContainer aspects = AspectsLoader.getAspects(client.world, focusedStack, false).orElse(null);
        if (aspects == null || aspects.isEmpty()) return;

        List<TooltipComponent> tooltipComponents = new ArrayList<>();
        tooltipComponents.add(new AspectTooltipComponent(aspects));
        ((DrawContextAccessor) context).callDrawTooltip(((ScreenAccessor) it).getTextRenderer(), tooltipComponents, x, y, HoveredTooltipPositioner.INSTANCE);

        ci.cancel();
    }
}

package ru.feytox.etherology.client.gui.ether;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.mixin.InGameHudAccessor;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class ExhaustionOverlay {

    private static final Identifier OUTLINE = EIdentifier.of("textures/misc/corruption_outline.png");

    public static void renderOverlay(DrawContext context, InGameHud hud) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        Float opacity = getOverlayOpacity(player);
        if (opacity == null) return;
        ((InGameHudAccessor) hud).callRenderOverlay(context, OUTLINE, opacity);
    }

    private static Float getOverlayOpacity(PlayerEntity player) {
        return EtherologyComponents.ETHER.maybeGet(player)
                .map(EtherComponent::getPoints)
                .filter(points -> points <= EtherComponent.EXHAUSTION_3)
                .map(points -> 1 - points / EtherComponent.EXHAUSTION_3)
                .orElse(null);
    }
}

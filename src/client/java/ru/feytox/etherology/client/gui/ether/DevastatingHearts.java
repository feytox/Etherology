package ru.feytox.etherology.client.gui.ether;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class DevastatingHearts {
    private static final Identifier FULL_HEART = EIdentifier.of("hud/heart/devastating_full");
    private static final Identifier FULL_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_full_blinking");
    private static final Identifier HALF_HEART = EIdentifier.of("hud/heart/devastating_half");
    private static final Identifier HALF_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_half_blinking");
    private static final Identifier HARDCORE_FULL_HEART = EIdentifier.of("hud/heart/devastating_hardcore_full");
    private static final Identifier HARDCORE_FULL_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_hardcore_full_blinking");
    private static final Identifier HARDCORE_HALF_HEART = EIdentifier.of("hud/heart/devastating_hardcore_half");
    private static final Identifier HARDCORE_HALF_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_hardcore_half_blinking");

    public static boolean hasCurse(PlayerEntity player) {
        return EtherologyComponents.ETHER.maybeGet(player).map(EtherComponent::isHasCurse).orElse(false);
    }

    @Nullable
    public static Identifier getDevastatingTexture(InGameHud.HeartType originalHeart, boolean hardcore, boolean half, boolean blinking) {
        var player = MinecraftClient.getInstance().player;
        if (originalHeart.equals(InGameHud.HeartType.CONTAINER) || player == null) return null;
        if (originalHeart.equals(InGameHud.HeartType.ABSORBING)) return null;
        if (!hasCurse(player)) return null;

        if (hardcore) {
            if (half) return blinking ? HARDCORE_HALF_BLINKING_HEART :  HARDCORE_HALF_HEART;
            return blinking ?  HARDCORE_FULL_BLINKING_HEART :  HARDCORE_FULL_HEART;
        }

        if (half) return blinking ? HALF_BLINKING_HEART : HALF_HEART;
        return blinking ? FULL_BLINKING_HEART : FULL_HEART;
    }
}

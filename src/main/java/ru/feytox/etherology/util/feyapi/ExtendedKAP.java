package ru.feytox.etherology.util.feyapi;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ExtendedKAP extends KeyframeAnimationPlayer {
    private final Identifier identifier;

    public ExtendedKAP(@NotNull KeyframeAnimation animation, Identifier identifier) {
        super(animation);
        this.identifier = identifier;
    }

    public Identifier getId() {
        return identifier;
    }
}

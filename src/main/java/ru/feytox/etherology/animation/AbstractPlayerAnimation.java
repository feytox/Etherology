package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.List;

@AllArgsConstructor
public abstract class AbstractPlayerAnimation {

    @Getter
    @NonNull
    private final Identifier animationId;


    @Getter
    private final boolean firstPerson;

    @Getter
    private final List<Identifier> replacements;

    @Getter
    private final boolean shouldBreak;

    public boolean play(IAnimatedPlayer player, int easeLength, @Nullable Ease ease) {
        return PlayerAnimationController.playAnimation(player, this, easeLength, ease);
    }
}

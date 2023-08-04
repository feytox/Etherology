package ru.feytox.etherology.animation.playerAnimation;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.custom.EtherRegistrable;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import java.util.List;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class AbstractPlayerAnimation implements EtherRegistrable {

    @NonNull
    private final Identifier animationId;
    private final boolean firstPerson;
    private final List<Identifier> replacements;
    private final boolean shouldBreak;
    @Nullable
    private final Consumer<EtherologyPlayer> startAction;
    @Nullable
    private final Consumer<EtherologyPlayer> endAction;

    public boolean play(EtherologyPlayer player, int easeLength, @Nullable Ease ease) {
        return PlayerAnimationController.playAnimation(player, this, easeLength, ease);
    }

    public void register() {
        EtherRegistrable.super.register(animationId);
    }
}

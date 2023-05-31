package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.custom.EtherRegistrable;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractPlayerAnimation implements EtherRegistrable {

    @Getter
    @NonNull
    private final Identifier animationId;


    @Getter
    private final boolean firstPerson;

    @Getter
    private final List<Identifier> replacements;

    @Getter
    private final boolean shouldBreak;

    @Getter
    @Nullable
    private final Consumer<IAnimatedPlayer> startAction;

    @Getter
    @Nullable
    private final Consumer<IAnimatedPlayer> endAction;

    public boolean play(IAnimatedPlayer player, int easeLength, @Nullable Ease ease) {
        return PlayerAnimationController.playAnimation(player, this, easeLength, ease);
    }

    public void register() {
        EtherRegistrable.super.register(animationId);
    }
}

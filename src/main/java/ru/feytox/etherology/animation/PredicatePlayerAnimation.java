package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PredicatePlayerAnimation extends AbstractPlayerAnimation {

    @NonNull
    private final Predicate<AbstractClientPlayerEntity> playPredicate;

    @Getter
    private final int easeLength;

    @Getter
    @Nullable
    private final Ease ease;

    public PredicatePlayerAnimation(Identifier animationId, int easeLength, @Nullable Ease ease, boolean firstPerson, boolean shouldBreak, @NotNull Predicate<AbstractClientPlayerEntity> playPredicate, @Nullable Consumer<IAnimatedPlayer> startAction, @Nullable Consumer<IAnimatedPlayer> endAction, Identifier... replacements) {
        super(animationId, firstPerson, List.of(replacements), shouldBreak, startAction, endAction);
        this.playPredicate = playPredicate;
        this.ease = ease;
        this.easeLength = easeLength;
    }

    public PredicatePlayerAnimation(Identifier animationId, int easeLength, @Nullable Ease ease, boolean firstPerson, boolean shouldBreak, @NotNull Predicate<AbstractClientPlayerEntity> playPredicate, Identifier... replacements) {
        this(animationId, easeLength, ease, firstPerson, shouldBreak, playPredicate, null, null, replacements);
    }

    public PredicatePlayerAnimation(Identifier animationId, int easeLength, @Nullable Ease ease, boolean firstPerson, boolean shouldBreak, @NotNull Predicate<AbstractClientPlayerEntity> playPredicate, @Nullable Consumer<IAnimatedPlayer> startAction, Identifier... replacements) {
        this(animationId, easeLength, ease, firstPerson, shouldBreak, playPredicate, startAction, null, replacements);
    }

    public void play(IAnimatedPlayer player) {
        play(player, easeLength, ease);
    }

    @Override
    public boolean play(IAnimatedPlayer player, int easeLength, @Nullable Ease ease) {
        boolean result = super.play(player, easeLength, ease);
        player.setAnimState(this, result);
        return result;
    }

    public boolean test(AbstractClientPlayerEntity clientPlayerEntity) {
        return playPredicate.test(clientPlayerEntity);
    }
}

package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

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

    public PredicatePlayerAnimation(Identifier animationId, int easeLength, @Nullable Ease ease, boolean firstPerson, boolean shouldBreak, @NotNull Predicate<AbstractClientPlayerEntity> playPredicate, @Nullable Consumer<EtherologyPlayer> startAction, @Nullable Consumer<EtherologyPlayer> endAction, Identifier... replacements) {
        super(animationId, firstPerson, List.of(replacements), shouldBreak, startAction, endAction);
        this.playPredicate = playPredicate;
        this.ease = ease;
        this.easeLength = easeLength;
    }

    public PredicatePlayerAnimation(Identifier animationId, int easeLength, @Nullable Ease ease, boolean firstPerson, boolean shouldBreak, @NotNull Predicate<AbstractClientPlayerEntity> playPredicate, Identifier... replacements) {
        this(animationId, easeLength, ease, firstPerson, shouldBreak, playPredicate, null, null, replacements);
    }

    public PredicatePlayerAnimation(Identifier animationId, int easeLength, @Nullable Ease ease, boolean firstPerson, boolean shouldBreak, @NotNull Predicate<AbstractClientPlayerEntity> playPredicate, @Nullable Consumer<EtherologyPlayer> startAction, Identifier... replacements) {
        this(animationId, easeLength, ease, firstPerson, shouldBreak, playPredicate, startAction, null, replacements);
    }

    public void play(EtherologyPlayer player) {
        play(player, easeLength, ease);
    }

    @Override
    public boolean play(EtherologyPlayer player, int easeLength, @Nullable Ease ease) {
        boolean result = super.play(player, easeLength, ease);
        player.etherology$setAnimState(this, result);
        return result;
    }

    public boolean test(AbstractClientPlayerEntity clientPlayerEntity) {
        return playPredicate.test(clientPlayerEntity);
    }
}

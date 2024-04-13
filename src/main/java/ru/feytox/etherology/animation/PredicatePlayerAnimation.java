package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.misc.EtherologyPlayer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a Etherology animation based on a predicate.
 */
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

    /**
     * Plays the Etherology animation for the player.
     * @param  player	player
     */
    public void play(EtherologyPlayer player) {
        play(player, easeLength, ease);
    }

    /**
     * Plays the Etherology animation for the given player with the specified ease length and ease.
     *
     * @param player      the EtherologyPlayer to play the animation for
     * @param easeLength  the length of the ease
     * @param ease        the ease to apply to the animation (nullable)
     * @return            the result of playing the animation
     */
    @Override
    public boolean play(EtherologyPlayer player, int easeLength, @Nullable Ease ease) {
        boolean result = super.play(player, easeLength, ease);
        player.etherology$setAnimState(this, result);
        return result;
    }

    /**
     * Tests the animation predicate for the given player.
     * 
     * @param player  the EtherologyPlayer to test animation predicate
     * @return        true if the animation should be played; false otherwise
     */
    public boolean test(AbstractClientPlayerEntity player) {
        return playPredicate.test(player);
    }
}

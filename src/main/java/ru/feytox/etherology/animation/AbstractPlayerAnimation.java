package ru.feytox.etherology.animation;

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

/**
 * This is an abstract class for player animations.
 * It provides methods to play and register animations with PlayerAnimator.
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class AbstractPlayerAnimation implements EtherRegistrable {

    /**
     * The identifier of the animation.
     */
    @NonNull
    private final Identifier animationId;

    /**
     * Indicates whether the animation is first-person.
     */
    private final boolean firstPerson;

    /**
     * The list of replacements for the animation.
     */
    private final List<Identifier> replacements;

    /**
     * Indicates whether the animation should break.
     */
    private final boolean shouldBreak;

    /**
     * The action to be performed when the animation starts.
     */
    @Nullable
    private final Consumer<EtherologyPlayer> startAction;

    /**
     * The action to be performed when the animation ends.
     */
    @Nullable
    private final Consumer<EtherologyPlayer> endAction;

    /**
     * Plays the animation for the given player.
     *
     * @param player      the player object
     * @param easeLength  the ease length
     * @param ease        the ease type
     * @return true if the animation was played successfully, false otherwise
     */
    public boolean play(EtherologyPlayer player, int easeLength, @Nullable Ease ease) {
        return PlayerAnimationController.playAnimation(player, this, easeLength, ease);
    }

    /**
     * Registers the animation.
     */
    public void register() {
        EtherRegistrable.super.register(animationId);
    }
}

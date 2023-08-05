package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

/**
 * Represents a keyframe animation player with extended information for Etherology animations.
 */
public class EtherKeyframe extends KeyframeAnimationPlayer {
    @Getter
    private final AbstractPlayerAnimation playerAnimation;
    @Nullable
    private Runnable endAction = null;

    /**
     * Constructs a new EtherKeyframe with the specified PlayerAnimator's animation and Etherology animation.
     *
     * @param animation        the keyframe animation
     * @param playerAnimation  the player animation
     */
    public EtherKeyframe(@NotNull KeyframeAnimation animation, AbstractPlayerAnimation playerAnimation) {
        super(animation);
        this.playerAnimation = playerAnimation;
    }

    /**
     * Sets up the end action for specified player.
     *
     * @param player  the EtherologyPlayer
     */
    public void setupEndAction(EtherologyPlayer player) {
        if (playerAnimation.getEndAction() == null) return;
        endAction = () -> playerAnimation.getEndAction().accept(player);
    }

    @Override
    public void tick() {
        super.tick();
        if (getCurrentTick() >= getStopTick() - 3) {
            if (endAction != null) endAction.run();
        }
    }
}

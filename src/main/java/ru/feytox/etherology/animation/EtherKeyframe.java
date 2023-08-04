package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

public class EtherKeyframe extends KeyframeAnimationPlayer {
    private final AbstractPlayerAnimation playerAnimation;
    @Nullable
    private Runnable endAction = null;

    public EtherKeyframe(@NotNull KeyframeAnimation animation, AbstractPlayerAnimation playerAnimation) {
        super(animation);
        this.playerAnimation = playerAnimation;
    }

    public AbstractPlayerAnimation getAnim() {
        return playerAnimation;
    }

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

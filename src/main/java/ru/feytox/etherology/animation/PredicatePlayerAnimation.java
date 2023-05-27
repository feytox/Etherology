package ru.feytox.etherology.animation;

import com.google.common.collect.Lists;
import dev.kosmx.playerAnim.core.util.Ease;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.List;
import java.util.function.Predicate;

public class PredicatePlayerAnimation extends AbstractPlayerAnimation {

    @NonNull
    private final Predicate<AbstractClientPlayerEntity> playPredicate;

    @Getter
    private final int easeLength;

    @Getter
    @Nullable
    private final Ease ease;

    public PredicatePlayerAnimation(Identifier animationId, int easeLength, @Nullable Ease ease, boolean firstPerson, boolean shouldBreak, @NotNull Predicate<AbstractClientPlayerEntity> playPredicate, AbstractPlayerAnimation... replacements) {
        super(animationId, firstPerson, Lists.transform(List.of(replacements), AbstractPlayerAnimation::getAnimationId), shouldBreak);
        this.playPredicate = playPredicate;
        this.ease = ease;
        this.easeLength = easeLength;
    }

    public void play(IAnimatedPlayer player) {
        boolean result = play(player, easeLength, ease);
        player.setAnimState(this, result);
    }

    public boolean test(AbstractClientPlayerEntity clientPlayerEntity) {
        return playPredicate.test(clientPlayerEntity);
    }

    public PredicatePlayerAnimation register() {
        return PlayerAnimationController.register(this);
    }
}

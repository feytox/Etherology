package ru.feytox.etherology.animation;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a Etherology animation based on a predicate.
 */
public class TriggerPlayerAnimation extends AbstractPlayerAnimation {

    public TriggerPlayerAnimation(Identifier animationId, boolean firstPerson, boolean shouldBreak, @Nullable Consumer<EtherologyPlayer> startAction, @Nullable Consumer<EtherologyPlayer> endAction, Identifier... replacements) {
        super(animationId, firstPerson, List.of(replacements), shouldBreak, startAction, endAction);
    }
}

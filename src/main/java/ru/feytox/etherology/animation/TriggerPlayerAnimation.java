package ru.feytox.etherology.animation;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.List;
import java.util.function.Consumer;

public class TriggerPlayerAnimation extends AbstractPlayerAnimation {

    public TriggerPlayerAnimation(Identifier animationId, boolean firstPerson, boolean shouldBreak, @Nullable Consumer<IAnimatedPlayer> startAction, @Nullable Consumer<IAnimatedPlayer> endAction, Identifier... replacements) {
        super(animationId, firstPerson, List.of(replacements), shouldBreak, startAction, endAction);
    }

    public TriggerPlayerAnimation(Identifier animationId, boolean firstPerson, boolean shouldBreak, Identifier... replacements) {
        this(animationId, firstPerson, shouldBreak, null, null, replacements);
    }
}

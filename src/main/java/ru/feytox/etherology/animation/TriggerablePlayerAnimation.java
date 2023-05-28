package ru.feytox.etherology.animation;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.List;
import java.util.function.Consumer;

public class TriggerablePlayerAnimation extends AbstractPlayerAnimation {

    public TriggerablePlayerAnimation(Identifier animationId, boolean firstPerson, boolean shouldBreak, @Nullable Consumer<IAnimatedPlayer> endAction, AbstractPlayerAnimation... replacements) {
        super(animationId, firstPerson, List.of(replacements), shouldBreak, endAction);
    }

    public TriggerablePlayerAnimation(Identifier animationId, boolean firstPerson, boolean shouldBreak, AbstractPlayerAnimation... replacements) {
        this(animationId, firstPerson, shouldBreak, null, replacements);
    }
}

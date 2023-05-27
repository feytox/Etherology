package ru.feytox.etherology.animation;

import net.minecraft.util.Identifier;

import java.util.List;

public class TriggerablePlayerAnimation extends AbstractPlayerAnimation {

    public TriggerablePlayerAnimation(Identifier animationId, boolean firstPerson, boolean shouldBreak, Identifier... replacements) {
        super(animationId, firstPerson, List.of(replacements), shouldBreak);
    }
}

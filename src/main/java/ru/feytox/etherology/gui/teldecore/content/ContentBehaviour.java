package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.serialization.Codec;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Predicate;

@RequiredArgsConstructor
public enum ContentBehaviour implements StringIdentifiable {
    ALWAYS(hasQuest -> true),
    BEFORE(hasQuest -> hasQuest),
    AFTER(hasQuest -> !hasQuest);

    public static final Codec<ContentBehaviour> CODEC = StringIdentifiable.createBasicCodec(ContentBehaviour::values);

    private final Predicate<Boolean> behaviourTest;

    public boolean test(boolean hasQuest) {
        return behaviourTest.test(hasQuest);
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}

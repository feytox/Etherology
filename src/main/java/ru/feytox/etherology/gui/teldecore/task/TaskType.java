package ru.feytox.etherology.gui.teldecore.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;

@Getter
@RequiredArgsConstructor
public enum TaskType {
    SEARCH(EIdentifier.of("textures/gui/teldecore/quest/quest_search.png"), 0),
    ITEM(EIdentifier.of("textures/gui/teldecore/quest/quest_item.png"), 16);

    private final Identifier texture;
    private final int dy;
}

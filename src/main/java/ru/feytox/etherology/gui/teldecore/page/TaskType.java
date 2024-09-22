package ru.feytox.etherology.gui.teldecore.page;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor
public enum TaskType {
    SEARCH(EIdentifier.of("textures/gui/teldecore/quest/quest_search.png"), 0),
    ITEM(EIdentifier.of("textures/gui/teldecore/quest/quest_item.png"), 16);

    private final Identifier texture;
    @Getter
    private final int dy;

    public void render(DrawContext context) {
        context.drawTexture(texture, 0, dy, 0, 0, 118, 12, 118, 12);
    }
}

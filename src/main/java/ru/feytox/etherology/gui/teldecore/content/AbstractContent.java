package ru.feytox.etherology.gui.teldecore.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.font.TextRenderer;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;

@RequiredArgsConstructor @Getter
public abstract class AbstractContent {

    private final float offsetUp;
    private final float offsetDown;

    public abstract float getHeight(TextRenderer textRenderer);
    public abstract ParentedWidget toWidget(TeldecoreScreen parent, float x, float y);
}

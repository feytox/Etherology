package ru.feytox.etherology.gui.teldecore.page;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EmptyPage extends AbstractPage {

    private static final Identifier EMPTY = EIdentifier.of("textures/gui/teldecore/page/empty.png");

    public EmptyPage(TeldecoreScreen parent, boolean isLeft) {
        super(parent, EMPTY, isLeft, 4, 192);
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {}
}

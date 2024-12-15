package ru.feytox.etherology.client.gui.teldecore.page;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EmptyPage extends AbstractPage {

    private static final Identifier EMPTY = EIdentifier.of("textures/gui/teldecore/page/empty.png");

    public EmptyPage(TeldecoreScreen parent, boolean isLeft) {
        super(parent, EMPTY, isLeft, 10, 186);
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {}
}

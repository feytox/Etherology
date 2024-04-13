package ru.feytox.etherology.gui.teldecore;


import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import ru.feytox.etherology.util.deprecated.UwuLib;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EmptyBook extends VerticalFlowLayout {
    public EmptyBook() {
        super(Sizing.fixed(310), Sizing.fixed(210));
        this.surface(UwuLib.tiled(new EIdentifier("textures/gui/teldecore/teldecore_book.png"),
                0, 0, 309, 209, 512, 512));
        this.verticalAlignment(VerticalAlignment.TOP);
        this.horizontalAlignment(HorizontalAlignment.LEFT);
    }
}

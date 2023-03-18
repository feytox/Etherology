package ru.feytox.etherology.gui.teldecore.pages;

import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;

public class DoublePage extends VerticalFlowLayout {
    public DoublePage(EmptyPage leftPage, EmptyPage rightPage) {
        super(Sizing.fixed(286), Sizing.fixed(196));
        this.child(
                leftPage.positioning(Positioning.relative(0, 0))
        ).child(
                rightPage.positioning(Positioning.relative(100, 0))
        );
    }
}

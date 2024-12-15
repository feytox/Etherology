package ru.feytox.etherology.client.gui.teldecore.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;

public abstract class ParentedWidget implements Drawable, Element, Selectable {

    protected final TeldecoreScreen parent;
    protected final TextRenderer textRenderer;
    protected float baseX;
    protected float baseY;
    protected boolean focused = false;

    public ParentedWidget(TeldecoreScreen parent, float baseX, float baseY) {
        this.parent = parent;
        this.textRenderer = parent.getTextRenderer();
        this.baseX = baseX;
        this.baseY = baseY;
    }

    public ParentedWidget(TeldecoreScreen parent) {
        this(parent, parent.getX(), parent.getY());
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    public <T extends Element & Drawable & Selectable> void addDrawableChild(T drawableChild) {
        parent.addDrawableChild(drawableChild);
    }
}

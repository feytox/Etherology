package ru.feytox.etherology.gui.teldecore.pages;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.buttons.PageButton;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.UwuLib;

public class EmptyPage extends VerticalFlowLayout {
    ButtonComponent last_prev_button;
    ButtonComponent last_next_button;
    boolean is_left;
    Identifier identifier;

    public EmptyPage(boolean is_left, Identifier identifier) {
        super(Sizing.fixed(143), Sizing.fixed(196));
        this.identifier = identifier;
        this.surface(getPageTexture(identifier, is_left));
        this.is_left = is_left;

        if (is_left) {
            ButtonComponent prev_button = PageButton.getButtonForPage(true);
            if (prev_button != null) {
                this.child(prev_button);
                this.last_prev_button = prev_button;
            }
        } else {
            ButtonComponent next_button = PageButton.getButtonForPage(false);
            if (next_button != null) {
                this.child(next_button);
                this.last_next_button = next_button;
            }
        }

        this.is_left = is_left;
    }

    public void refreshButtons() {
        if (this.is_left) {
            ButtonComponent prev_button = PageButton.getButtonForPage(true);
            if (last_prev_button != null) {
                this.removeChild(last_prev_button);
            }
            if (prev_button != null) {
                this.child(prev_button);
                this.last_prev_button = prev_button;
            }
            return;
        }

        ButtonComponent next_button = PageButton.getButtonForPage(false);
        if (last_next_button != null) {
            this.removeChild(last_next_button);
        }
        if (next_button != null) {
            this.child(next_button);
            this.last_next_button = next_button;
        }
    }

    public EmptyPage(boolean is_left) {
        this(is_left, new EIdentifier("textures/gui/teldecore/teldecore_empty.png"));
    }


    public static Surface getPageTexture(Identifier identifier, boolean is_left) {
        return UwuLib.tiled(identifier, is_left ? 0 : 143, 0, is_left ? 142 : 285, 195, 286, 196);
    }
}

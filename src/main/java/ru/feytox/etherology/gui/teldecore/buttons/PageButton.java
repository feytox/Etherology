package ru.feytox.etherology.gui.teldecore.buttons;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.teldecore.Chapters;
import ru.feytox.etherology.util.deprecated.UwuLib;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.function.Consumer;

public class PageButton extends ButtonComponent {

    public static Chapters chapters = new Chapters();

    public PageButton(boolean is_prev) {
        super(Text.empty(), getOnClick(is_prev));
        this.sizing(Sizing.fixed(24), Sizing.fixed(20));
        this.positioning(Positioning.absolute(is_prev ? -2 : 121, 175));
    }

    public PageButton prevButton() {
        this.renderer = UwuLib.texture(new EIdentifier("textures/gui/teldecore/teldecore_book.png"),
                311, 1, 334, 20, 512, 512);
        return this;
    }

    public PageButton nextButton() {
        this.renderer = UwuLib.texture(new EIdentifier("textures/gui/teldecore/teldecore_book.png"),
                337, 1, 360, 20, 512, 512);
        return this;
    }

    private static Consumer<ButtonComponent> getOnClick(boolean is_prev) {
        return (ButtonComponent button) -> {
            if (is_prev) {
                chapters.current().prevTwo();
                return;
            }
            chapters.current().nextTwo();
        };
    }


    @Nullable
    public static ButtonComponent getButtonForPage(boolean is_prev) {
        if (is_prev) {
            if (chapters == null) {
                return null;
            }

            if (chapters.current().isFirst() && !chapters.isFirst()) {
                return new BigPageButton(true).prevButton();
            } else if (!chapters.current().isFirst()) {
                return new PageButton(true).prevButton();
            }
            return null;
        }

        if (chapters == null) {
            return new BigPageButton(false).nextButton();
        }

        if (chapters.current().isLast() && !chapters.isLast()) {
            return new BigPageButton(false).nextButton();
        } else if (!chapters.current().isLast()) {
            return new PageButton(false).nextButton();
        }
        // TODO: сделать что-то, что будет в самом конце книги
        return null;
    }
}

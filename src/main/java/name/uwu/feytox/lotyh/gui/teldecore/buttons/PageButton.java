package name.uwu.feytox.lotyh.gui.teldecore.buttons;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import name.uwu.feytox.lotyh.util.LIdentifier;
import name.uwu.feytox.lotyh.client.LotyhClient;
import name.uwu.feytox.lotyh.util.UwuLib;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PageButton extends ButtonComponent {

    public PageButton(boolean is_prev) {
        super(Text.empty(), getOnClick(is_prev));
        this.sizing(Sizing.fixed(24), Sizing.fixed(20));
        this.positioning(Positioning.absolute(is_prev ? -2 : 121, 175));
    }

    public PageButton prevButton() {
        this.renderer = UwuLib.texture(new LIdentifier("textures/gui/teldecore/teldecore_book.png"),
                311, 1, 334, 20, 512, 512);
        return this;
    }

    public PageButton nextButton() {
        this.renderer = UwuLib.texture(new LIdentifier("textures/gui/teldecore/teldecore_book.png"),
                337, 1, 360, 20, 512, 512);
        return this;
    }

    private static Consumer<ButtonComponent> getOnClick(boolean is_prev) {
        return (ButtonComponent button) -> {
            if (is_prev) {
                LotyhClient.chapters.current().prevTwo();
                return;
            }
            LotyhClient.chapters.current().nextTwo();
        };
    }


    @Nullable
    public static ButtonComponent getButtonForPage(boolean is_prev) {
        if (is_prev) {
            if (LotyhClient.chapters == null) {
                return null;
            }

            if (LotyhClient.chapters.current().isFirst() && !LotyhClient.chapters.isFirst()) {
                return new BigPageButton(true).prevButton();
            } else if (!LotyhClient.chapters.current().isFirst()) {
                return new PageButton(true).prevButton();
            }
            return null;
        }

        if (LotyhClient.chapters == null) {
            return new BigPageButton(false).nextButton();
        }

        if (LotyhClient.chapters.current().isLast() && !LotyhClient.chapters.isLast()) {
            return new BigPageButton(false).nextButton();
        } else if (!LotyhClient.chapters.current().isLast()) {
            return new PageButton(false).nextButton();
        }
        // TODO: сделать что-то, что будет в самом конце книги
        return null;
    }
}

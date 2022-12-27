package name.uwu.feytox.lotyh.gui.teldecore.buttons;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import name.uwu.feytox.lotyh.util.LIdentifier;
import name.uwu.feytox.lotyh.client.LotyhClient;
import name.uwu.feytox.lotyh.util.UwuLib;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class BigPageButton extends ButtonComponent {

    public BigPageButton(boolean is_prev) {
        super(Text.empty(), getOnClick(is_prev));
        this.sizing(Sizing.fixed(24), Sizing.fixed(20));
        this.positioning(Positioning.absolute(is_prev ? -2 : 121, 175));
    }

    public BigPageButton prevButton() {
        this.renderer = UwuLib.bigTexture(new LIdentifier("textures/gui/teldecore/teldecore_book.png"),
                311, 23, 334, 42, 512, 512);
        return this;
    }

    public BigPageButton nextButton() {
        this.renderer = UwuLib.bigTexture(new LIdentifier("textures/gui/teldecore/teldecore_book.png"),
                337, 23, 360, 42, 512, 512);
        return this;
    }

    private static Consumer<ButtonComponent> getOnClick(boolean is_prev) {
        return (ButtonComponent button) -> {
            if (is_prev) {
                LotyhClient.chapters.prev();
                return;
            }
            LotyhClient.chapters.next();
        };
    }
}

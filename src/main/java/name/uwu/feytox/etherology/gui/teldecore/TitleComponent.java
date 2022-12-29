package name.uwu.feytox.etherology.gui.teldecore;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Positioning;
import net.minecraft.text.Text;

public class TitleComponent extends LabelComponent {
    public TitleComponent(String s) {
        super(Text.of(s));
        this.color(Color.ofRgb(6768690))
                .positioning(Positioning.relative(50, 5));
    }
}

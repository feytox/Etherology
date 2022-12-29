package name.uwu.feytox.etherology.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface IBooleanComponent extends ComponentV3 {
    boolean getValue();
    void setValue(boolean value);
    void toggle();
}

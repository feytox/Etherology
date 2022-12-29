package name.uwu.feytox.etherology.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface IFloatComponent extends ComponentV3 {
    float getValue();
    void setValue(float value);
    void increment(float value);
    void decrement(float value);
}
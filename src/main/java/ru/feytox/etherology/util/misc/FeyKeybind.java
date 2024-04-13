package ru.feytox.etherology.util.misc;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class FeyKeybind extends KeyBinding {
    public FeyKeybind(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
    }

    public boolean isReleased() {
        return this.timesPressed != 0 && !isPressed();
    }
}

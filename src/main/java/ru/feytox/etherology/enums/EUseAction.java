package ru.feytox.etherology.enums;

import net.minecraft.util.UseAction;

public enum EUseAction {
    OCULUS_ETHEROLOGY,
    STAFF_ETHEROLOGY,
    TWOHANDHELD_ETHEROLOGY;

    public UseAction getUseAction() {
        return UseAction.valueOf(name());
    }
}

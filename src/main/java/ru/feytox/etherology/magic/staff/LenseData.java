package ru.feytox.etherology.magic.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class LenseData {

    @Getter @Setter
    private LenseMode lenseMode;

    // TODO: 21.11.2023 rename
    public enum LenseMode {
        OFF,
        UP,
        DOWN
    }
}

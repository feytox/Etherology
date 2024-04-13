package ru.feytox.etherology.util.misc;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;

public class EIdentifier extends Identifier {
    public EIdentifier(String path) {
        super(Etherology.MOD_ID, path);
    }
}

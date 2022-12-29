package name.uwu.feytox.etherology.util;

import name.uwu.feytox.etherology.Etherology;
import net.minecraft.util.Identifier;

public class EIdentifier extends Identifier {
    public EIdentifier(String path) {
        super(Etherology.MOD_ID, path);
    }
}

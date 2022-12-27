package name.uwu.feytox.lotyh.util;

import name.uwu.feytox.lotyh.Lotyh;
import net.minecraft.util.Identifier;

public class LIdentifier extends Identifier {
    public LIdentifier(String path) {
        super(Lotyh.MOD_ID, path);
    }
}

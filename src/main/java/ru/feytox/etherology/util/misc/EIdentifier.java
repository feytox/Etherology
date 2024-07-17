package ru.feytox.etherology.util.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;

@UtilityClass
public class EIdentifier {

    public static Identifier of(String path) {
        return Identifier.tryParse(Etherology.MOD_ID, path);
    }

    public static String strId(String id) {
        return Etherology.MOD_ID + ":" + id;
    }
}

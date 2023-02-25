package name.uwu.feytox.etherology.feyperms;

import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;

public abstract class PermissionDeserializer<T extends Permission> {
    private final String name;

    public PermissionDeserializer(String name) {
        this.name = name;
    }

    public abstract T deserialize(Identifier id, JsonElement json);

    public String getName() {
        return name;
    }
}

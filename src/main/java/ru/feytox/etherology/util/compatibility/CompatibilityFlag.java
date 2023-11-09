package ru.feytox.etherology.util.compatibility;

import lombok.RequiredArgsConstructor;
import net.fabricmc.loader.api.FabricLoader;

@RequiredArgsConstructor(staticName = "of")
public class CompatibilityFlag {

    private final String modId;

    public boolean isEnable() {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}

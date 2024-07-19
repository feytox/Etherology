package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import ru.feytox.etherology.data.ethersource.EtherSourceLoader;

@UtilityClass
public class ResourceReloaders {

    public static void registerServerData() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(EtherSourceLoader.INSTANCE);
    }
}

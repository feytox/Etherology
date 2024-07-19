package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import ru.feytox.etherology.data.aspects.AspectsLoader;
import ru.feytox.etherology.magic.aspects.AspectRegistryPart;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class RegistriesRegistry {

    public static RegistryKey<Registry<AspectRegistryPart>> ASPECTS = RegistryKey.ofRegistry(EIdentifier.of("aspects"));

    public static void registerAll() {
        DynamicRegistries.registerSynced(ASPECTS, AspectRegistryPart.CODEC);

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> AspectsLoader.clearCache());
    }
}

package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import ru.feytox.etherology.data.aspectContainer.AspectContainerLoader;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.data.Tab;
import ru.feytox.etherology.magic.aspectContainer.AspectContainerRegistryPart;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class RegistriesRegistry {

    public static RegistryKey<Registry<AspectContainerRegistryPart>> ASPECT_CONTAINER = RegistryKey.ofRegistry(EIdentifier.of("aspect_container"));
    public static RegistryKey<Registry<Chapter>> CHAPTERS = RegistryKey.ofRegistry(EIdentifier.of("chapters"));
    public static RegistryKey<Registry<Tab>> TABS = RegistryKey.ofRegistry(EIdentifier.of("tabs"));

    public static void registerAll() {
        DynamicRegistries.registerSynced(ASPECT_CONTAINER, AspectContainerRegistryPart.CODEC);
        DynamicRegistries.registerSynced(CHAPTERS, Chapter.CODEC);
        DynamicRegistries.registerSynced(TABS, Tab.CODEC);

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> AspectContainerLoader.clearCache());
    }
}

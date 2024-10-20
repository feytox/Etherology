package ru.feytox.etherology.registry.misc;


import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.registry.SculkSensorFrequencyRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import ru.feytox.etherology.block.seal.SealBlockRenderer;
import ru.feytox.etherology.item.revelationView.RevelationViewRenderer;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class EventsRegistry {

    public static RegistryEntry.Reference<GameEvent> RESONANCE = registerGameEvent("etherology_resonance", 16);

    public static void registerClientSide() {
        // TODO: 07.07.2024 move
        RevelationViewRenderer.registerRendering();
        SealBlockRenderer.registerRendering();
    }

    public static void registerGameEvents() {
        SculkSensorFrequencyRegistry.register(RESONANCE.registryKey(), 10);
    }

    private static RegistryEntry.Reference<GameEvent> registerGameEvent(String id, int range) {
        return Registry.registerReference(Registries.GAME_EVENT, EIdentifier.of(id), new GameEvent(range));
    }
}

package ru.feytox.etherology.registry.misc;


import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.registry.SculkSensorFrequencyRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.event.GameEvent;
import ru.feytox.etherology.item.revelationView.RevelationViewRenderer;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class EventsRegistry {

    public static GameEvent RESONANCE = registerGameEvent("etherology_resonance", 16);

    public static void registerClientSide() {
        RevelationViewRenderer.registerRendering();
    }

    public static void registerGameEvents() {
        SculkSensorFrequencyRegistry.register(RESONANCE, 10);
    }

    private static GameEvent registerGameEvent(String id, int range) {
        return Registry.register(Registries.GAME_EVENT, new EIdentifier(id), new GameEvent(id, range));
    }
}

package ru.feytox.etherology.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.world.event.GameEvent;
import ru.feytox.etherology.registry.misc.EventsRegistry;

import java.util.concurrent.CompletableFuture;

public class GameEventTagGeneration extends FabricTagProvider<GameEvent> {

    public GameEventTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.GAME_EVENT, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(GameEventTags.VIBRATIONS).add(EventsRegistry.RESONANCE.registryKey());
        getOrCreateTagBuilder(GameEventTags.WARDEN_CAN_LISTEN).add(EventsRegistry.RESONANCE.registryKey());
    }
}

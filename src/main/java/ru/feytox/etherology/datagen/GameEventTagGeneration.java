package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.GameEventTags;
import ru.feytox.etherology.registry.misc.EventsRegistry;

import java.util.concurrent.CompletableFuture;

public class GameEventTagGeneration extends FabricTagProvider.GameEventTagProvider {

    public GameEventTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(GameEventTags.VIBRATIONS).add(EventsRegistry.RESONANCE);
        getOrCreateTagBuilder(GameEventTags.WARDEN_CAN_LISTEN).add(EventsRegistry.RESONANCE);
    }
}

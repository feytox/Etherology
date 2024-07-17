package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.registry.RegistryKeys.*;
import static ru.feytox.etherology.Etherology.MOD_ID;

public class RegistriesGeneration extends FabricDynamicRegistryProvider {
    public RegistriesGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        Stream.of(CONFIGURED_FEATURE, PLACED_FEATURE, STRUCTURE_SET, STRUCTURE, TEMPLATE_POOL, PROCESSOR_LIST,
                        BIOME, ENCHANTMENT)
                .map(registries::getWrapperOrThrow)
                .forEach(entries::addAll);
    }

    @Override
    public String getName() {
        return MOD_ID;
    }
}

package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.registry.misc.EtherEnchantments.PEAL;
import static ru.feytox.etherology.registry.misc.EtherEnchantments.REFLECTION;

public class EnchantmentTagGeneration extends FabricTagProvider.EnchantmentTagProvider {

    public EnchantmentTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(EnchantmentTags.NON_TREASURE).add(PEAL, REFLECTION);
    }
}

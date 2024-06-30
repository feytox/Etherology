package ru.feytox.etherology.mixin;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.OverworldBiomeCreator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OverworldBiomeCreator.class)
public interface OverworldBiomeCreatorAccessor {
    @Invoker
    static void callAddBasicFeatures(GenerationSettings.LookupBackedBuilder generationSettings) {
        throw new UnsupportedOperationException();
    }
}

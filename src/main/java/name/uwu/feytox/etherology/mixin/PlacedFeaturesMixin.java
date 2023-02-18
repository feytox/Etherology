package name.uwu.feytox.etherology.mixin;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static name.uwu.feytox.etherology.generations.features.zones.EssenceZoneFeature.*;

@Mixin(PlacedFeatures.class)
public class PlacedFeaturesMixin {

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void onBootstrap(Registerable<PlacedFeature> featureRegisterable, CallbackInfo ci) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
        RegistryEntry<ConfiguredFeature<?, ?>> ketaEntry = registryEntryLookup.getOrThrow(KETA_CONF_ZONE);
        RegistryEntry<ConfiguredFeature<?, ?>> relaEntry = registryEntryLookup.getOrThrow(RELA_CONF_ZONE);
        RegistryEntry<ConfiguredFeature<?, ?>> closEntry = registryEntryLookup.getOrThrow(CLOS_CONF_ZONE);
        RegistryEntry<ConfiguredFeature<?, ?>> viaEntry = registryEntryLookup.getOrThrow(VIA_CONF_ZONE);

        PlacedFeatures.register(featureRegisterable, KETA_ZONE_KEY, ketaEntry, BiomePlacementModifier.of(),
                RarityFilterPlacementModifier.of(100));
        PlacedFeatures.register(featureRegisterable, RELA_ZONE_KEY, relaEntry, BiomePlacementModifier.of(),
                RarityFilterPlacementModifier.of(100));
        PlacedFeatures.register(featureRegisterable, CLOS_ZONE_KEY, closEntry, BiomePlacementModifier.of(),
                RarityFilterPlacementModifier.of(100));
        PlacedFeatures.register(featureRegisterable, VIA_ZONE_KEY, viaEntry, BiomePlacementModifier.of(),
                RarityFilterPlacementModifier.of(100));
    }
}

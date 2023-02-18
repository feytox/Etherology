package name.uwu.feytox.etherology.util.feyapi;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;

public class FeyFeatures {
    public static RegistryKey<ConfiguredFeature<?, ?>> ofConfigured(String id) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new EIdentifier(id));
    }

    public static RegistryKey<PlacedFeature> ofPlaced(String id) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new EIdentifier(id));
    }
}

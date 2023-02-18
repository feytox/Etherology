package name.uwu.feytox.etherology.mixin;

import name.uwu.feytox.etherology.generations.features.zones.EssenceZoneFeatureConfig;
import net.minecraft.registry.Registerable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static name.uwu.feytox.etherology.generations.features.zones.EssenceZoneFeature.*;

@Mixin(ConfiguredFeatures.class)
public class ConfiguredFeaturesMixin {

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void onBootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable, CallbackInfo ci) {
        ConfiguredFeatures.register(featureRegisterable, KETA_CONF_ZONE, ESSENCE_ZONE_FEATURE,
                new EssenceZoneFeatureConfig(0));
        ConfiguredFeatures.register(featureRegisterable, RELA_CONF_ZONE, ESSENCE_ZONE_FEATURE,
                new EssenceZoneFeatureConfig(1));
        ConfiguredFeatures.register(featureRegisterable, CLOS_CONF_ZONE, ESSENCE_ZONE_FEATURE,
                new EssenceZoneFeatureConfig(2));
        ConfiguredFeatures.register(featureRegisterable, VIA_CONF_ZONE, ESSENCE_ZONE_FEATURE,
                new EssenceZoneFeatureConfig(3));
    }
}

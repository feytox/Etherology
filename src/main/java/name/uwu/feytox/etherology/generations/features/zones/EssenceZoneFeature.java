package name.uwu.feytox.etherology.generations.features.zones;

import com.mojang.serialization.Codec;
import name.uwu.feytox.etherology.blocks.zone_blocks.ZoneCoreBlockEntity;
import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import name.uwu.feytox.etherology.util.feyapi.UwuLib;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.FeatureContext;

import static name.uwu.feytox.etherology.BlocksRegistry.ZONE_CORE_BLOCK;

public class EssenceZoneFeature extends Feature<EssenceZoneFeatureConfig> {
    public static final EIdentifier ESSENCE_ZONE_FEATURE_ID = new EIdentifier("essence_zone_feature");
    public static Feature<EssenceZoneFeatureConfig> ESSENCE_ZONE_FEATURE = new EssenceZoneFeature(EssenceZoneFeatureConfig.CODEC);
//    public static RegistryKey<ConfiguredFeature<?, ?>> KETA_CONF_ZONE = FeyFeatures.ofConfigured("keta_conf_zone");
//    public static RegistryKey<ConfiguredFeature<?, ?>> RELA_CONF_ZONE = FeyFeatures.ofConfigured("rela_conf_zone");
//    public static RegistryKey<ConfiguredFeature<?, ?>> CLOS_CONF_ZONE = FeyFeatures.ofConfigured("clos_conf_zone");
//    public static RegistryKey<ConfiguredFeature<?, ?>> VIA_CONF_ZONE = FeyFeatures.ofConfigured("via_conf_zone");
    public static RegistryKey<ConfiguredFeature<?, ?>> KETA_CONF_ZONE = ConfiguredFeatures.of("keta_conf_zone");
    public static RegistryKey<ConfiguredFeature<?, ?>> RELA_CONF_ZONE = ConfiguredFeatures.of("keta_conf_zone");
    public static RegistryKey<ConfiguredFeature<?, ?>> CLOS_CONF_ZONE = ConfiguredFeatures.of("keta_conf_zone");
    public static RegistryKey<ConfiguredFeature<?, ?>> VIA_CONF_ZONE = ConfiguredFeatures.of("keta_conf_zone");
//    public static RegistryKey<PlacedFeature> KETA_ZONE_KEY = FeyFeatures.ofPlaced("keta_zone");
//    public static RegistryKey<PlacedFeature> RELA_ZONE_KEY = FeyFeatures.ofPlaced("rela_zone");
//    public static RegistryKey<PlacedFeature> CLOS_ZONE_KEY = FeyFeatures.ofPlaced("clos_zone");
//    public static RegistryKey<PlacedFeature> VIA_ZONE_KEY = FeyFeatures.ofPlaced("via_zone");
    public static RegistryKey<PlacedFeature> KETA_ZONE_KEY = PlacedFeatures.of("keta_zone");
    public static RegistryKey<PlacedFeature> RELA_ZONE_KEY = PlacedFeatures.of("rela_zone");
    public static RegistryKey<PlacedFeature> CLOS_ZONE_KEY = PlacedFeatures.of("clos_zone");
    public static RegistryKey<PlacedFeature> VIA_ZONE_KEY = PlacedFeatures.of("via_zone");

    public EssenceZoneFeature(Codec<EssenceZoneFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<EssenceZoneFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        EssenceZoneFeatureConfig config = context.getConfig();

        EssenceZones zoneType = config.zone();

        // ground finder
        BlockPos surfacePos = UwuLib.getSurfacePos(origin, world);
        if (surfacePos == null) return false;

        BlockPos corePos = UwuLib.getSurfacePos(surfacePos.add(0, 5, 0), world);
        if (corePos == null) return false;

        BlockEntity blockEntity = UwuLib.getOrCreateBlockEntity(world, ZONE_CORE_BLOCK, corePos);
        if (blockEntity instanceof ZoneCoreBlockEntity zoneCore) {
            zoneCore.setup(zoneType, 128);

            return true;
        }
        return false;
    }
}

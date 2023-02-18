package name.uwu.feytox.etherology.world.features.zones;

import com.mojang.serialization.Codec;
import name.uwu.feytox.etherology.blocks.zone_blocks.ZoneCoreBlockEntity;
import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import name.uwu.feytox.etherology.util.feyapi.UwuLib;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import static name.uwu.feytox.etherology.BlocksRegistry.ZONE_CORE_BLOCK;

public class EssenceZoneFeature extends Feature<EssenceZoneFeatureConfig> {
    public static final EIdentifier ESSENCE_ZONE_FEATURE_ID = new EIdentifier("essence_zone_feature");
    public static Feature<EssenceZoneFeatureConfig> ESSENCE_ZONE_FEATURE = new EssenceZoneFeature(EssenceZoneFeatureConfig.CODEC);
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

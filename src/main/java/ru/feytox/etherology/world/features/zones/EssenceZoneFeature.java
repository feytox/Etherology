package ru.feytox.etherology.world.features.zones;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import ru.feytox.etherology.blocks.zone_blocks.ZoneCoreBlockEntity;
import ru.feytox.etherology.magic.zones.EssenceZones;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.FeyRandom;
import ru.feytox.etherology.util.feyapi.UwuLib;

import javax.annotation.Nullable;

import static ru.feytox.etherology.BlocksRegistry.ZONE_CORE_BLOCK;

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
        BlockPos corePos = null;
        switch (zoneType) {
            case KETA -> corePos = ketaGenerate(world, origin, random);
            case RELA -> corePos = relaGenerate(world, origin, random);
            case CLOS -> corePos = closGenerate(world, origin, random);
            case VIA -> corePos = viaGenerate(world, origin, random);
        }
        if (corePos == null) return false;

        BlockEntity blockEntity = UwuLib.getOrCreateBlockEntity(world, ZONE_CORE_BLOCK, corePos);
        if (blockEntity instanceof ZoneCoreBlockEntity zoneCore) {
            zoneCore.setup(zoneType, 128);
            return true;
        }
        return false;
    }

    @Nullable
    public BlockPos ketaGenerate(StructureWorldAccess world, BlockPos origin, Random random) {
        Biome biome = world.getBiome(origin).value();

        if (TagUtil.isIn(BiomeTags.IS_OCEAN, biome) || TagUtil.isIn(BiomeTags.IS_RIVER, biome)) {
            // поверхность воды
            if (random.nextDouble() >= 0.5) return null;
            return UwuLib.getSurfacePos(new BlockPos(origin.getX(), 60, origin.getZ()), world);
        } else if (TagUtil.isIn(BiomeTags.IS_END, biome)) {
            // острова в энде
            if (random.nextDouble() >= 0.33) return null;
            return UwuLib.getAirPos(new BlockPos(origin.getX(), 60, origin.getZ()), world);
        } else {
            // любой ледяной биом
            int y = getRandomY(60, random, 3);
            return UwuLib.getAirPos(new BlockPos(origin.getX(), y, origin.getZ()), world);
        }
    }

    public BlockPos relaGenerate(StructureWorldAccess world, BlockPos origin, Random random) {
        Biome biome = world.getBiome(origin).value();

        if (TagUtil.isIn(BiomeTags.IS_NETHER, biome)) {
            // незер
            if (random.nextDouble() >= 0.33) return null;
            int y = getRandomY(48, random, 5);
            return UwuLib.getAirPos(new BlockPos(origin.getX(), y, origin.getZ()), world);
        } else {
            // любой биом верхнего мира (y = от 0 до 128)
            int y = random.nextInt(128);
            return UwuLib.getAirPos(new BlockPos(origin.getX(), y, origin.getZ()), world);
        }
    }

    public BlockPos closGenerate(StructureWorldAccess world, BlockPos origin, Random random) {
        Biome biome = world.getBiome(origin).value();

        if (TagUtil.isIn(BiomeTags.IS_END, biome)) {
            // острова в энде
            if (random.nextDouble() >= 0.5) return null;
            return UwuLib.getAirPos(new BlockPos(origin.getX(), 60, origin.getZ()), world);
        } else {
            // горные биомы
            return UwuLib.getSurfacePos(new BlockPos(origin.getX(), 80, origin.getZ()), world);
        }
    }

    public BlockPos viaGenerate(StructureWorldAccess world, BlockPos origin, Random random) {
        Biome biomeOrigin = world.getBiome(origin).value();
        BlockPos surfacePos = UwuLib.getSurfacePos(new BlockPos(origin.getX(), 60, origin.getZ()), world);
        Biome biomeSurface = world.getBiome(surfacePos).value();

        if (TagUtil.isIn(BiomeTags.IS_NETHER, biomeOrigin)) {
            // незер
            if (random.nextDouble() >= 0.5) return null;
            int y = getRandomY(48, random, 5);
            return UwuLib.getAirPos(new BlockPos(origin.getX(), y, origin.getZ()), world);
        }

        if (biomeSurface.getTemperature() >= 2.0) {
            // жаркие биомы
            if (random.nextDouble() >= 0.5) return null;
            return surfacePos;
        } else {
            // любые биомы (y = от -64 до 0)
            // TODO: 19/02/2023 Исправь так, чтобы если выше 0 - бан
            int y = -random.nextInt(64);
            return UwuLib.getAirPos(new BlockPos(origin.getX(), y, origin.getZ()), world);
        }
    }

    public static int getRandomY(int startY, Random random, int steps) {
        int y = startY;
        for (int i = steps; i > 0; i--) {
            y += FeyRandom.getValueSign(random) * random.nextInt(5 * i);
        }
        return y;
    }
}

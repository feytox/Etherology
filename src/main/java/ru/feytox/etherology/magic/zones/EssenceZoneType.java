package ru.feytox.etherology.magic.zones;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.RGBColor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public enum EssenceZoneType implements StringIdentifiable {
    NOT_INITIALIZED,
    EMPTY,
    KETA(EssenceZoneType::ketaTest, new RGBColor(128, 205, 247), new RGBColor(105, 128, 231)),
    RELLA(EssenceZoneType::rellaTest, new RGBColor(177, 229,106), new RGBColor(106, 182, 81)),
    VIA(EssenceZoneType::viaTest, new RGBColor(248, 122, 95), new RGBColor(205, 58, 76)),
    CLOS(EssenceZoneType::closTest, new RGBColor(106, 182, 81), new RGBColor(208, 158, 89));

    private static final float RARE_CHANCE = 0.5f;
    private static final float VERY_RARE_CHANCE = 0.25f;

    @Nullable
    @Getter
    private final GenerationSetting generationSetting;

    @Nullable
    @Getter
    private final RGBColor startColor;

    @Nullable
    @Getter
    private final RGBColor endColor;

    EssenceZoneType() {
        this(null, null, null);
    }

    public static List<EssenceZoneType> getShuffledTypes(Random random) {
        List<EssenceZoneType> zoneTypes = ObjectArrayList.of(KETA, RELLA, VIA, CLOS);
        for (int i = zoneTypes.size(); i > 1; i--) {
            Collections.swap(zoneTypes, i-1, random.nextInt(i));
        }
        return zoneTypes;
    }

    private static Integer ketaTest(World world, BlockPos centerPos, Random random) {
        BlockPos surfacePos = getSurfacePos(world, centerPos);
        RegistryEntry<Biome> biome = world.getBiome(surfacePos);
        if (biome.value().isCold(surfacePos) && biome.isIn(BiomeTags.IS_OVERWORLD)) return surfacePos.getY();
        if ((biome.isIn(BiomeTags.IS_RIVER) || biome.isIn(BiomeTags.IS_OCEAN)) && random.nextFloat() <= RARE_CHANCE) return surfacePos.getY();
        if (biome.isIn(BiomeTags.IS_END) && random.nextFloat() <= VERY_RARE_CHANCE) return surfacePos.getY();
        return null;
    }

    private static Integer rellaTest(World world, BlockPos centerPos, Random random) {
        BlockPos zonePos = getRandomAirPos(world, centerPos, 0, 128, random);
        RegistryEntry<Biome> biome = world.getBiome(zonePos);
        if (biome.isIn(BiomeTags.IS_OVERWORLD)) return zonePos.getY();
        if (biome.isIn(BiomeTags.IS_NETHER) && random.nextFloat() <= RARE_CHANCE) return zonePos.getY();
        return null;
    }

    private static Integer viaTest(World world, BlockPos centerPos, Random random) {
        BlockPos surfacePos = getSurfacePos(world, centerPos);
        RegistryEntry<Biome> biome = world.getBiome(surfacePos);
        if (biome.value().isHot(surfacePos) && biome.isIn(BiomeTags.IS_OVERWORLD) && random.nextFloat() <= RARE_CHANCE) return surfacePos.getY();
        if (biome.isIn(BiomeTags.IS_NETHER) && random.nextFloat() <= RARE_CHANCE) return surfacePos.getY();

        BlockPos zonePos = getRandomAirPos(world, centerPos, -64, 0, random);
        biome = world.getBiome(zonePos);
        if (biome.isIn(BiomeTags.IS_OVERWORLD)) return zonePos.getY();
        return null;
    }

    private static Integer closTest(World world, BlockPos centerPos, Random random) {
        BlockPos surfacePos = getSurfacePos(world, centerPos);
        RegistryEntry<Biome> biome = world.getBiome(surfacePos);
        if (biome.isIn(BiomeTags.IS_OVERWORLD) && (surfacePos.getY() > 128 || biome.isIn(BiomeTags.IS_MOUNTAIN))) return surfacePos.getY();
        if (biome.isIn(BiomeTags.IS_END) && random.nextFloat() <= RARE_CHANCE) return surfacePos.getY();
        return null;
    }

    private static BlockPos getSurfacePos(World world, BlockPos blockPos) {
        return world.getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos);
    }

    private static BlockPos getRandomAirPos(World world, BlockPos blockPos, int bottomY, int topY, Random random) {
        BlockPos lastPos = blockPos;
        for (int i = 0; i < 5; i++) {
            lastPos = new BlockPos(blockPos.getX(), random.nextBetween(bottomY, topY), blockPos.getZ());
            if (world.isAir(lastPos)) return lastPos;
        }
        return lastPos;
    }

    public boolean isZone() {
        return !this.equals(EMPTY) && !this.equals(NOT_INITIALIZED);
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }

    @FunctionalInterface
    public interface GenerationSetting {
        @Nullable
        Integer test(World world, BlockPos centerPos, Random random);
    }
}

package ru.feytox.etherology.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.structure.Structure;
import ru.feytox.etherology.registry.world.WorldGenRegistry;

@RequiredArgsConstructor(staticName = "of")
public class StructurePlacementModifier extends AbstractConditionalPlacementModifier {

    private final RegistryKey<Structure> structureKey;
    private final int chunkRadius;
    private final boolean exclude;

    public static final Codec<StructurePlacementModifier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Identifier.CODEC.fieldOf("structure").forGetter(modifier -> modifier.structureKey.getValue()),
                            Codecs.NONNEGATIVE_INT.fieldOf("chunk_radius").forGetter(modifier -> modifier.chunkRadius),
                            Codec.BOOL.fieldOf("exclude").forGetter(modifier -> modifier.exclude))
                    .apply(instance, (id, radius, exclude) -> of(RegistryKey.of(RegistryKeys.STRUCTURE, id), radius, exclude))
            );

    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        Structure structure = context.getWorld().getRegistryManager().get(RegistryKeys.STRUCTURE).get(structureKey);
        if (chunkRadius == 0) return exclude ^ context.getWorld().getChunk(pos).getStructureStart(structure) != null;

        boolean result = ChunkPos.stream(new ChunkPos(pos), chunkRadius)
                .anyMatch(chunkPos -> context.getWorld().getChunk(chunkPos.x, chunkPos.z).getStructureStart(structure) != null);
        return result != exclude;
    }

    @Override
    public PlacementModifierType<?> getType() {
        return WorldGenRegistry.STRUCTURE_PLACEMENT_MODIFIER;
    }
}

package ru.feytox.etherology.world.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import ru.feytox.etherology.registry.world.WorldGenRegistry;

import java.util.List;
import java.util.function.Function;

public class RotatedPoolElement extends SinglePoolElement {

    private static final RegistryEntry<StructureProcessorList> EMPTY_PROCESSORS;
    public static final MapCodec<RotatedPoolElement> CODEC;

    protected final BlockRotation rotation;

    private RotatedPoolElement(Either<Identifier, StructureTemplate> location, RegistryEntry<StructureProcessorList> processors, StructurePool.Projection projection, BlockRotation rotation) {
        super(location, processors, projection);
        this.rotation = rotation;
    }

    @Override
    public Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation) {
        return super.getStart(structureTemplateManager, this.rotation);
    }

    @Override
    public boolean generate(StructureTemplateManager structureTemplateManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos pivot, BlockRotation rotation, BlockBox box, Random random, boolean keepJigsaws) {
        return super.generate(structureTemplateManager, world, structureAccessor, chunkGenerator, pos, pivot, this.rotation, box, random, keepJigsaws);
    }

    @Override
    public BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation) {
        return super.getBoundingBox(structureTemplateManager, pos, this.rotation);
    }

    @Override
    protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, boolean keepJigsaws) {
        return super.createPlacementData(this.rotation, box, keepJigsaws);
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> getStructureBlockInfos(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random) {
        return super.getStructureBlockInfos(structureTemplateManager, pos, this.rotation, random);
    }

    public static Function<StructurePool.Projection, RotatedPoolElement> of(Identifier id, BlockRotation rotation) {
        return of(id, rotation, EMPTY_PROCESSORS);
    }

    public static Function<StructurePool.Projection, RotatedPoolElement> of(Identifier id, BlockRotation rotation, RegistryEntry<StructureProcessorList> processors) {
        return projection -> new RotatedPoolElement(Either.left(id), processors, projection, rotation);
    }

    protected static <E extends RotatedPoolElement> RecordCodecBuilder<E, BlockRotation> rotationGetter() {
        return BlockRotation.CODEC.fieldOf("rotation").forGetter(element -> element.rotation);
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return WorldGenRegistry.ROTATED_POOL_ELEMENT;
    }

    static {
        EMPTY_PROCESSORS = RegistryEntry.of(new StructureProcessorList(List.of()));
        CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(locationGetter(), processorsGetter(), projectionGetter(), rotationGetter())
                        .apply(instance, RotatedPoolElement::new));
    }
}

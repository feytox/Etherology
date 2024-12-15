package ru.feytox.etherology.block.sedimentary;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.seal.SealBlockEntity;
import ru.feytox.etherology.magic.seal.EssenceConsumer;
import ru.feytox.etherology.magic.seal.EssenceSupplier;
import ru.feytox.etherology.magic.seal.SealType;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.subtype.SparkSubtype;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK_ENTITY;

public class SedimentaryStoneBlockEntity extends TickableBlockEntity implements EssenceConsumer {

    private static final float MAX_POINTS = 32.0f;
    private static final float POINTS_DELTA = MAX_POINTS * EssenceLevel.getFullnessDelta();
    private static final float CONSUME_CHANCE = 0.85f;
    @Nullable
    private EssenceSupplier cachedSeal;
    private float points = 0;

    public SedimentaryStoneBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, null);
    }

    public SedimentaryStoneBlockEntity(BlockPos pos, BlockState state, @Nullable EssenceLevel essenceLevel) {
        super(SEDIMENTARY_BLOCK_ENTITY, pos, state);
        if (essenceLevel == null) return;

        points = MAX_POINTS * essenceLevel.toFullness();
        markDirty();
    }

    public boolean onUseAxe(BlockState state, World world, SealType sealType, Vec3i sideVector, boolean dropItem) {
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f, true);

        if (!(world instanceof ServerWorld serverWorld)) {
            spawnSedimentaryParticle(world, pos, sealType);
            return true;
        }

        points = Math.max(0, points - POINTS_DELTA);
        syncData(serverWorld);
        updateBlockState(serverWorld, state, points < 1e-9 ? SealType.EMPTY : sealType);

        if (!dropItem)
            return true;

        BlockPos itemPos = pos.add(sideVector);
        Optional<Item> match = sealType.getPrimoShard();
        match.ifPresent(item -> ItemScatterer.spawn(serverWorld, itemPos.getX(), itemPos.getY(), itemPos.getZ(), item.getDefaultStack()));
        return true;
    }

    private static void spawnSedimentaryParticle(World world, BlockPos blockPos, SealType sealType) {
        if (!sealType.isSeal()) return;
        SparkSubtype sparkType = SparkSubtype.of(sealType);
        if (sparkType == null) return;

        Vec3d centerPos = blockPos.toCenterPos();
        Vec3d diffPos = new Vec3d(0.8d, 0.8d, 0.8d);
        List<Vec3d> fullPoses = listOf(centerPos.add(diffPos), centerPos.subtract(diffPos), 0.1d);
        List<Vec3d> particlePoses = fullPoses.stream().filter(vec3d ->
                Math.abs(centerPos.y - vec3d.x) >= 0.7 || Math.abs(centerPos.y - vec3d.y) >= 0.7 || Math.abs(centerPos.z - vec3d.z) >= 0.7).toList();

        Random random = world.getRandom();
        particlePoses.forEach(pos -> {
            if (random.nextDouble() > 0.005) return;
            SparkParticleEffect effect = new SparkParticleEffect(EtherParticleTypes.SPARK, pos.add(0, -0.2, 0), sparkType);
            effect.spawnParticles(world, 1, 0, pos);
        });
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (!state.get(SedimentaryStone.POWERED)) return;
        SedimentaryStone.executeOnStone(state, stone -> consumingTick(world, stone, state));
    }

    private void consumingTick(ServerWorld world, SedimentaryStone sedimentary, BlockState state) {
        if (points >= MAX_POINTS || world.getTime() % 170 != 0) return;
        if (world.getRandom().nextFloat() > CONSUME_CHANCE) return;
        tickConsuming(world, pos, sedimentary.getSealType()).ifPresent(seal -> updateBlockState(world, state, seal));
    }

    private void updateBlockState(ServerWorld world, BlockState state, SealType newSealType) {
        EssenceLevel level = EssenceLevel.fromFullness(points / MAX_POINTS);
        SealType sealType = level.isPresent() ? newSealType : SealType.EMPTY;
        BlockState newState = SedimentaryStone.transformState(state, sealType, level);
        if (!state.equals(newState)) world.setBlockState(pos, newState);
    }

    @Override
    public float getConsumingValue() {
        return Math.min(0.5f, MAX_POINTS - points);
    }

    @Override
    public void incrementEssence(float value) {
        points += value;
        points = Math.min(MAX_POINTS, points);
    }

    @Override
    public Optional<EssenceSupplier> getCachedSeal() {
        return Optional.ofNullable(cachedSeal);
    }

    @Override
    public void setCachedSeal(EssenceSupplier seal) {
        cachedSeal = seal;
    }

    @Override
    public int getRadius() {
        return SealBlockEntity.MAX_RADIUS;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putFloat("points", points);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        points = nbt.getFloat("points");
    }

    private static List<Vec3d> listOf(Vec3d startPos, Vec3d endPos, double step) {
        // TODO: 26.03.2024 optimize
        List<Vec3d> result = new ArrayList<>();

        double minX = Math.min(startPos.x, endPos.x);
        double maxX = Math.max(startPos.x, endPos.x);
        double minY = Math.min(startPos.y, endPos.y);
        double maxY = Math.max(startPos.y, endPos.y);
        double minZ = Math.min(startPos.z, endPos.z);
        double maxZ = Math.max(startPos.z, endPos.z);

        for (double x = minX; x <= maxX; x += step) {
            for (double y = minY; y <= maxY; y += step) {
                for (double z = minZ; z <= maxZ; z += step) {
                    result.add(new Vec3d(x, y, z));
                }
            }
        }
        return result;
    }
}

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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.seal.SealBlockEntity;
import ru.feytox.etherology.magic.seal.EssenceConsumer;
import ru.feytox.etherology.magic.seal.EssenceSupplier;
import ru.feytox.etherology.magic.seal.SealType;
import ru.feytox.etherology.particle.info.SparkSedimentaryInfo;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

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
            SparkSedimentaryInfo.spawnSedimentaryParticle(world, pos, sealType);
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
}

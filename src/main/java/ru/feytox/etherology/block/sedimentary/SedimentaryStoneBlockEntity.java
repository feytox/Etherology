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
import ru.feytox.etherology.block.zone.ZoneCoreBlockEntity;
import ru.feytox.etherology.magic.zones.EssenceConsumer;
import ru.feytox.etherology.magic.zones.EssenceSupplier;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.info.SparkSedimentaryInfo;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.Optional;

import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK_ENTITY;

public class SedimentaryStoneBlockEntity extends TickableBlockEntity implements EssenceConsumer {

    private static final float MAX_POINTS = 32.0f;
    private static final float CONSUME_CHANCE = 0.85f;
    @Nullable
    private EssenceSupplier cachedZone;
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

    public boolean onUseAxe(BlockState state, World world, EssenceZoneType zoneType, Vec3i sideVector) {
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f, true);

        if (!(world instanceof ServerWorld serverWorld)) {
            SparkSedimentaryInfo.spawnSedimentaryParticle(world, pos, zoneType);
            return true;
        }

        points = 0;
        syncData(serverWorld);
        updateBlockState(serverWorld, state, EssenceZoneType.EMPTY);

        BlockPos itemPos = pos.add(sideVector);
        Optional<Item> match = zoneType.getPrimoShard();
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
        tickConsuming(world, pos, sedimentary.getZoneType()).ifPresent(zone -> updateBlockState(world, state, zone));
    }

    private void updateBlockState(ServerWorld world, BlockState state, EssenceZoneType newZoneType) {
        EssenceLevel level = EssenceLevel.fromFullness(points / MAX_POINTS);
        EssenceZoneType zoneType = level.isPresent() ? newZoneType : EssenceZoneType.EMPTY;
        BlockState newState = SedimentaryStone.transformState(state, zoneType, level);
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
    public Optional<EssenceSupplier> getCachedZone() {
        return Optional.ofNullable(cachedZone);
    }

    @Override
    public void setCachedZone(EssenceSupplier zoneCore) {
        cachedZone = zoneCore;
    }

    @Override
    public int getRadius() {
        return ZoneCoreBlockEntity.MAX_RADIUS;
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

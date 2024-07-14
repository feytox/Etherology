package ru.feytox.etherology.block.sedimentary;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceConsumer;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.info.SparkSedimentaryInfo;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.Optional;

import static ru.feytox.etherology.block.sedimentary.SedimentaryStone.ESSENCE_LEVEL;
import static ru.feytox.etherology.block.sedimentary.SedimentaryStone.ESSENCE_STATE;
import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK;
import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK_ENTITY;

public class SedimentaryStoneBlockEntity extends TickableBlockEntity implements EssenceConsumer {

    private static final float MAX_POINTS = 32.0f;
    private float points = 0;
    private boolean validated = false;

    public SedimentaryStoneBlockEntity(BlockPos pos, BlockState state) {
        super(SEDIMENTARY_BLOCK_ENTITY, pos, state);
    }

    public boolean onUseAxe(BlockState state, World world, Vec3i sideVector) {
        if (state.get(ESSENCE_LEVEL) < 4) return false;
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f, true);
        EssenceZoneType zoneType = state.get(ESSENCE_STATE);

        if (!(world instanceof ServerWorld serverWorld)) {
            SparkSedimentaryInfo.spawnSedimentaryParticle(world, pos, zoneType);
            return true;
        }

        points = 0;
        syncData(serverWorld);
        updateBlockState(serverWorld, state, EssenceZoneType.EMPTY);

        BlockPos itemPos = pos.add(sideVector);
        Optional<Item> match = Registries.ITEM.getOrEmpty(EIdentifier.of("primoshard_" + zoneType.name().toLowerCase()));
        match.ifPresent(item ->
                ItemScatterer.spawn(serverWorld, itemPos.getX(), itemPos.getY(), itemPos.getZ(), item.getDefaultStack()));

        return true;
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        validateState(world, state);
        consumingTick(world, state);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        tickZoneParticles(world, blockPos, state.get(ESSENCE_STATE));
    }

    private void validateState(ServerWorld world, BlockState state) {
        if (validated) return;
        EssenceZoneType zoneType = state.get(ESSENCE_STATE);
        int essenceLevel = state.get(ESSENCE_LEVEL);
        if (points != 0.0f || !zoneType.isZone() || essenceLevel == 0) validated = true;
        if (validated) return;

        points = essenceLevel * MAX_POINTS;
        syncData(world);
    }

    private void consumingTick(ServerWorld world, BlockState state) {
        if (world.getTime() % 200 != 0 || points >= MAX_POINTS) return;
        EssenceZoneType zoneType = tickConsuming(world, pos, state.get(ESSENCE_STATE)).orElse(null);
        updateBlockState(world, state, zoneType);
    }

    private void updateBlockState(ServerWorld world, BlockState state, @Nullable EssenceZoneType newZoneType) {
        float k = Math.min(points / MAX_POINTS, 1.0f);
        BlockState newState = SEDIMENTARY_BLOCK.getDefaultState();
        if (k > 0) newState = state.with(ESSENCE_LEVEL, MathHelper.floor(4*k));
        if (newZoneType != null) newState = newState.with(ESSENCE_STATE, newZoneType);

        world.setBlockState(pos, newState);
    }

    @Override
    public float getConsumingValue() {
        return 0.5f;
    }

    @Override
    public void increment(float value) {
        points += value;
        points = Math.min(MAX_POINTS, points);
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

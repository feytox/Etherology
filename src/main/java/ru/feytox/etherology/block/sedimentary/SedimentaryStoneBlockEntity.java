package ru.feytox.etherology.block.sedimentary;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceConsumer;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.info.SedimentarySparkInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.Optional;

import static ru.feytox.etherology.block.sedimentary.SedimentaryStone.ESSENCE_LEVEL;
import static ru.feytox.etherology.block.sedimentary.SedimentaryStone.ESSENCE_STATE;
import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK;
import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK_ENTITY;

public class SedimentaryStoneBlockEntity extends TickableBlockEntity implements EssenceConsumer {
    private static final float MAX_POINTS = 32.0f;
    private float points = 0;

    public SedimentaryStoneBlockEntity(BlockPos pos, BlockState state) {
        super(SEDIMENTARY_BLOCK_ENTITY, pos, state);
    }

    public boolean onUseAxe(BlockState state, World world) {
        if (state.get(ESSENCE_LEVEL) < 4) return false;
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f, true);
        EssenceZoneType zoneType = state.get(ESSENCE_STATE);

        if (world.isClient) {
            SedimentarySparkInfo.spawnSedimentaryParticle(world, pos, zoneType);
            return true;
        }

        points = 0;
        markDirty();
        updateBlockState((ServerWorld) world, state, EssenceZoneType.EMPTY);

        Optional<Item> match = Registries.ITEM.getOrEmpty(new EIdentifier("primoshard_" + zoneType.name().toLowerCase()));
        match.ifPresent(item ->
                ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), item.getDefaultStack()));

        return true;
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        consumingTick(world, state);
    }

    public void consumingTick(ServerWorld world, BlockState state) {
        if (world.getTime() % 200 != 0 || points >= MAX_POINTS) return;
        EssenceZoneType zoneType = tickConsuming(world, pos, state.get(ESSENCE_STATE)).orElse(null);
        updateBlockState(world, state, zoneType);
    }

    public void updateBlockState(ServerWorld world, BlockState state, @Nullable EssenceZoneType newZoneType) {
        float k = points / MAX_POINTS;
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
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("points", points);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        points = nbt.getFloat("points");
    }
}

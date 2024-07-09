package ru.feytox.etherology.magic.lens;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;

import java.util.Map;

// TODO: 09.07.2024 consider to replace with chunk component
// TODO: rename and maybe move
public class RedstoneLensEffects extends PersistentState {

    private static final Type<RedstoneLensEffects> TYPE = new Type<>(RedstoneLensEffects::new, RedstoneLensEffects::createFromNbt, null);

    private final Map<BlockPos, RedstoneLensEffect> effects = new Object2ObjectOpenHashMap<>();

    public void addUsage(ServerWorld world, BlockPos pos, int power, int tickDelay) {
        boolean shouldUpdate = getUsage(pos) == null;
        effects.put(pos, new RedstoneLensEffect(power, tickDelay, false));

        markDirty();
        if (shouldUpdate) updateBlockAndNeighbors(world, pos);
    }

    public void addUsage(BlockPos pos, int power, int tickDelay) {
        effects.put(pos, new RedstoneLensEffect(power, tickDelay, getUsage(pos) == null));
    }

    @Nullable
    public RedstoneLensEffect getUsage(BlockPos pos) {
        if (!effects.containsKey(pos)) return null;

        RedstoneLensEffect effect = effects.get(pos);
        return effect.ticks > 0 ? effect : null;
    }

    public void tick(ServerWorld world) {
        if (effects.isEmpty()) return;

        effects.entrySet().removeIf(entry -> {
            BlockPos pos = entry.getKey();
            RedstoneLensEffect effect = entry.getValue();
            if (effect.resetShouldUpdate()) updateBlockAndNeighbors(world, pos);
            if (!effect.tick()) return false;

            updateBlockAndNeighbors(world, pos);
            return true;
        });
    }

    private void updateBlockAndNeighbors(ServerWorld world, BlockPos pos) {
        world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
        world.updateNeighbor(pos, Blocks.AIR, pos.up());
    }

    public static RedstoneLensEffects getServerState(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();

        RedstoneLensEffects effects = manager.getOrCreate(TYPE, Etherology.MOD_ID + "_redstone_lens");
        effects.markDirty();
        return effects;
    }

    public static RedstoneLensEffects createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound root = nbt.getCompound("effects");
        RedstoneLensEffects effects = new RedstoneLensEffects();

        root.getKeys().forEach(key -> {
            long longPos = Long.parseLong(key);
            BlockPos pos = BlockPos.fromLong(longPos);
            NbtCompound effectNbt = root.getCompound(key);
            int power = effectNbt.getInt("power");
            int ticks = effectNbt.getInt("ticks");
            effects.addUsage(pos, power, ticks);
            effects.markDirty();
        });

        return effects;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound root = new NbtCompound();
        effects.forEach((pos, effect) -> {
            String key = String.valueOf(pos.asLong());
            NbtCompound effectNbt = new NbtCompound();
            effectNbt.putInt("power", effect.power);
            effectNbt.putInt("ticks", effect.ticks);
            root.put(key, effectNbt);
        });

        nbt.put("effects", root);
        return nbt;
    }

    @Getter
    @AllArgsConstructor
    public static final class RedstoneLensEffect {
        private int power;
        private int ticks;
        private boolean shouldUpdate;

        /**
         * Decrements the tick counter and checks if it has reached
         * zero or less.
         *
         * @return     True if the tick counter is zero or less,
         *             False otherwise.
         */
        private boolean tick() {
            return ticks-- <= 0;
        }

        private boolean resetShouldUpdate() {
            if (!shouldUpdate) return false;
            shouldUpdate = false;
            return true;
        }
    }
}

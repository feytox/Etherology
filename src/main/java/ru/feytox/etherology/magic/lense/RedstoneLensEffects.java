package ru.feytox.etherology.magic.lense;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;

import java.util.Map;

// TODO: rename and maybe move
public class RedstoneLensEffects extends PersistentState {

    private Map<BlockPos, RedstoneLensEffect> effects = new Object2ObjectOpenHashMap<>();

    public void addUsage(ServerWorld world, BlockPos pos, int power, int tickDelay) {
        effects.put(pos, new RedstoneLensEffect(power, tickDelay));
        updateBlockAndNeighbors(world, pos);
        markDirty();
    }

    @Nullable
    public RedstoneLensEffect getUsage(BlockPos pos) {
        if (!effects.containsKey(pos)) return null;

        RedstoneLensEffect effect = effects.get(pos);
        if (effect.ticks > 0) return effect;

        effects.remove(pos);
        markDirty();
        return null;
    }

    public void tick(ServerWorld world) {
        if (effects.isEmpty()) return;

        Map<BlockPos, RedstoneLensEffect> newEffects = new Object2ObjectOpenHashMap<>();
        effects.entrySet().stream()
                .filter(entry -> {
                    RedstoneLensEffect effect = entry.getValue();
                    if (!effect.tick()) return true;

                    BlockPos pos = entry.getKey();
                    updateBlockAndNeighbors(world, pos);
                    return false;
                })
                .forEach(entry -> newEffects.put(entry.getKey(), entry.getValue()));

        effects = newEffects;
    }

    private void updateBlockAndNeighbors(ServerWorld world, BlockPos pos) {
        world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
        world.updateNeighbor(pos, Blocks.AIR, pos.up());
    }

    public static RedstoneLensEffects getServerState(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(nbt -> createFromNbt(world, nbt), RedstoneLensEffects::new, Etherology.MOD_ID + "_redstone_lens");
    }

    public static RedstoneLensEffects createFromNbt(ServerWorld world, NbtCompound nbt) {
        NbtCompound root = nbt.getCompound("effects");
        RedstoneLensEffects effects = new RedstoneLensEffects();

        root.getKeys().forEach(key -> {
            long longPos = Long.parseLong(key);
            BlockPos pos = BlockPos.fromLong(longPos);
            NbtCompound effectNbt = root.getCompound(key);
            int power = effectNbt.getInt("power");
            int ticks = effectNbt.getInt("ticks");
            effects.addUsage(world, pos, power, ticks);
        });

        return effects;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
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

        private boolean tick() {
            return ticks-- <= 0;
        }
    }
}

package ru.feytox.etherology.magic.corruption;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.util.EtherologyComponents;

@RequiredArgsConstructor
public class CorruptionComponent implements ServerTickingComponent, AutoSyncedComponent {
    private static final int TICK_RATE = 20;
    private static final float MIN_CORRUPTION = 1 / 256f;
    public static final float MAX_CHUNK_CORRUPTION = 64.0f;

    private final Chunk chunk;

    @Nullable
    @Getter
    private Corruption corruption;

    private int ticks;

    public void setCorruption(@Nullable Corruption corruption) {
        this.corruption = corruption;
        save();
    }

    public void save() {
        this.chunk.setNeedsSaving(true);
        EtherologyComponents.CORRUPTION.sync(chunk);
    }

    public void increment(float value) {
        if (value <= MIN_CORRUPTION) return;
        setCorruption(corruption == null ? new Corruption(value) : corruption.increment(value));
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbt) {
        NbtCompound corruptionNbt = nbt.getCompound("Corruption");
        corruption = Corruption.readFromNbt(corruptionNbt);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbt) {
        NbtCompound corruptionNbt = new NbtCompound();
        if (corruption != null && !corruption.isEmpty()) corruption.writeNbt(corruptionNbt);
        nbt.put("Corruption", corruptionNbt);
    }

    @Override
    public void serverTick() {
        if (ticks++ % TICK_RATE != 0) return;
        if (corruption == null || corruption.getCorruptionValue() <= MAX_CHUNK_CORRUPTION) return;
        if (!(chunk instanceof WorldChunk worldChunk)) return;

        World world = worldChunk.getWorld();
        float tipValue = (corruption.getCorruptionValue() - MAX_CHUNK_CORRUPTION) / 4;
        ChunkPos chunkPos = worldChunk.getPos();
        corruption = new Corruption(MAX_CHUNK_CORRUPTION);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == z || x == -z) continue;
                Chunk sideChunk = world.getChunk(x + chunkPos.x, z + chunkPos.z);
                CorruptionComponent component = sideChunk.getComponent(EtherologyComponents.CORRUPTION);
                component.increment(tipValue);
            }
        }

        save();
    }
}

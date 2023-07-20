package ru.feytox.etherology.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.registry.util.EtherologyComponents;

@RequiredArgsConstructor
public class CorruptionComponent implements ServerTickingComponent, AutoSyncedComponent {

    private final Chunk chunk;

    @Nullable
    @Getter
    private Corruption corruption;

    private int ticks;

    public void setCorruption(@Nullable Corruption corruption) {
        this.corruption = corruption;
        this.chunk.setNeedsSaving(true);
        EtherologyComponents.CORRUPTION.sync(chunk);
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
        if (ticks++ % 20 != 0) return;
    }
}

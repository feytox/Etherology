package ru.feytox.etherology.magic.zones;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.wispforest.owo.nbt.NbtKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.util.EtherologyComponents;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ZoneComponent implements ServerTickingComponent, AutoSyncedComponent {

    // ~8 in 64x64 chunks (4096 chunks or 1024x1024 blocks)
    private static final float INIT_CHANCE = 1 / 111.0f;
    private static final float MAX_VALUE = 128.0f;
    private static final int Y_RADIUS = 7;

    private final Chunk chunk;

    @Getter
    private EssenceZoneType zoneType = EssenceZoneType.NOT_INITIALIZED;

    @Nullable
    @Getter
    private EssenceZone essenceZone = null;

    @Nullable
    @Getter
    private Integer zoneY = null;

    /**
     * Retrieves the ZoneComponent associated with the given Chunk.
     *
     * @param  chunk  the Chunk to retrieve the ZoneComponent from
     * @return        the ZoneComponent associated with the Chunk, or null if it doesn't exist
     */
    @Nullable
    public static ZoneComponent getZone(Chunk chunk) {
        Optional<ZoneComponent> zoneOptional = EtherologyComponents.ESSENCE_ZONE.maybeGet(chunk);
        return zoneOptional.orElse(null);
    }

    /**
     * Decrements the given value from the essence zone.
     *
     * @param  dValue  the value to decrement
     * @return         the difference between the original value and the new value of the essence zone
     */
    public float decrement(float dValue) {
        if (essenceZone == null) return 0.0f;
        float value = essenceZone.getValue();
        float newValue = Math.max(0, value - dValue);
        essenceZone.setValue(newValue);
        float result = value - newValue;
        if (result != 0) {
            save();
            updateStatus();
        }

        return result;
    }

    public int getZoneRadius() {
        return Y_RADIUS;
    }

    public boolean isEmpty() {
        return !zoneType.isZone();
    }

    public void updateStatus() {
        if (isEmpty()) return;
        if (essenceZone != null && essenceZone.getValue() > 0) return;

        essenceZone = null;
        zoneType = EssenceZoneType.EMPTY;
        save();
    }

    @Override
    public void serverTick() {
        if (zoneType.equals(EssenceZoneType.NOT_INITIALIZED)) {
            initTick();
        }
    }

    private void initTick() {
        if (!(chunk instanceof WorldChunk worldChunk)) return;
        World world = worldChunk.getWorld();
        Random random = world.getRandom();
        if (random.nextFloat() >= INIT_CHANCE) {
            markAsEmpty();
            return;
        }

        BlockPos centerPos = worldChunk.getPos().getCenterAtY(64);
        List<EssenceZoneType> zoneTypes = EssenceZoneType.getShuffledTypes(random);

        for (EssenceZoneType zoneType : zoneTypes) {
            val generationSetting = zoneType.getGenerationSetting();
            if (generationSetting == null) continue;
            Integer zoneY = generationSetting.test(world, centerPos, random);
            if (zoneY == null) continue;

            markAsInitialized(zoneType, zoneY);
            // TODO: 01.12.2023 remove
            log.info("{} zone generated at {} {} {}", zoneType.name(), centerPos.getX(), zoneY, centerPos.getZ());
            return;
        }
        markAsEmpty();
    }

    /**
     * Marks the essence zone as empty.
     */
    private void markAsEmpty() {
        zoneType = EssenceZoneType.EMPTY;
        save();
    }

    /**
     * Marks the essence zone as initialized.
     */
    private void markAsInitialized(EssenceZoneType zoneType, int zoneY) {
        this.zoneType = zoneType;
        // TODO: 24.07.2023 change value
        this.essenceZone = new EssenceZone(MAX_VALUE);
        this.zoneY = zoneY;
        save();
    }

    public float getFillPercent() {
        if (isEmpty() || essenceZone == null) return 0.0f;
        return essenceZone.getValue() / MAX_VALUE;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbt) {
        zoneType = EssenceZoneType.valueOf(nbt.getString("zone_type"));
        essenceZone = EssenceZone.readFromNbt(nbt.getCompound("zone"));
        zoneY = nbt.getOr(new NbtKey<>("y", NbtKey.Type.INT), null);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbt) {
        nbt.putString("zone_type", zoneType.name());
        NbtCompound zoneNbt = new NbtCompound();
        if (essenceZone != null) essenceZone.writeNbt(zoneNbt);
        nbt.put("zone", zoneNbt);
        if (zoneY != null) nbt.putInt("y", zoneY);
    }

    /**
     * Saves the chunk and synchronizes the essence zone.
     */
    public void save() {
        this.chunk.setNeedsSaving(true);
        EtherologyComponents.ESSENCE_ZONE.sync(chunk);
    }
}

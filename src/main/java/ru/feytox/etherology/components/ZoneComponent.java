package ru.feytox.etherology.components;

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
import ru.feytox.etherology.enums.EssenceZoneType;
import ru.feytox.etherology.magic.zones.EssenceZone;
import ru.feytox.etherology.registry.util.EtherologyComponents;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ZoneComponent implements ServerTickingComponent, AutoSyncedComponent {

    // 4 in 64x64 chunks (4096 chunks or 1024x1024 blocks)
    private static final float INIT_CHANCE = 1 / 222.25f;

    private final Chunk chunk;

    @Getter
    private EssenceZoneType zoneType = EssenceZoneType.NOT_INITIALIZED;

    @Nullable
    @Getter
    private EssenceZone essenceZone = null;

    @Nullable
    @Getter
    private Integer zoneY = null;

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
            log.info("{} zone generated at {} {} {}", zoneType.name(), centerPos.getX(), zoneY, centerPos.getZ());
            return;
        }
        markAsEmpty();
    }

    private void markAsEmpty() {
        zoneType = EssenceZoneType.EMPTY;
        save();
    }

    private void markAsInitialized(EssenceZoneType zoneType, int zoneY) {
        this.zoneType = zoneType;
        this.essenceZone = new EssenceZone(64.0f);
        this.zoneY = zoneY;
        save();
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

    public void save() {
        this.chunk.setNeedsSaving(true);
        EtherologyComponents.ESSENCE_ZONE.sync(chunk);
    }
}

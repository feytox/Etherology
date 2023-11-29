package ru.feytox.etherology.registry.util;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.components.*;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lense.LensComponent;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EtherologyComponents implements EntityComponentInitializer, ChunkComponentInitializer, ItemComponentInitializer {
    public static final ComponentKey<IFloatComponent> ETHER_MAX =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_max"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_REGEN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_regen"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_POINTS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_points"), IFloatComponent.class);
    public static final ComponentKey<CorruptionComponent> CORRUPTION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("corruption"), CorruptionComponent.class);

    public static final ComponentKey<ZoneComponent> ESSENCE_ZONE =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("essence_zone"), ZoneComponent.class);

    public static final ComponentKey<LensComponent> LENS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("lens"), LensComponent.class);

    public static final ComponentKey<StaffComponent> STAFF =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("staff"), StaffComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ETHER_MAX, player -> new FloatComponent(player, 20), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(ETHER_REGEN, player -> new FloatComponent(player, 0.001F), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(ETHER_POINTS, player -> new EtherComponent(player, 15), RespawnCopyStrategy.LOSSLESS_ONLY);
    }

    @Override
    public void registerChunkComponentFactories(@NotNull ChunkComponentFactoryRegistry registry) {
        registry.register(CORRUPTION, CorruptionComponent::new);
        registry.register(ESSENCE_ZONE, ZoneComponent::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.register(item -> item instanceof LensItem || item.equals(ToolItems.STAFF), LENS, LensComponent::new);
        registry.register(ToolItems.STAFF, STAFF, StaffComponent::new);
    }

    public static ComponentKey<IFloatComponent> fromString(String key) {
        switch (key) {
            case "ETHER_MAX" -> {
                return ETHER_MAX;
            }
            case "ETHER_REGEN" -> {
                return ETHER_REGEN;
            }
            case "ETHER_POINTS" -> {
                return ETHER_POINTS;
            }
        }
        return null;
    }
}
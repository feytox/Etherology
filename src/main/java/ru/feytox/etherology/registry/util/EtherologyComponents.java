package ru.feytox.etherology.registry.util;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.components.CorruptionComponent;
import ru.feytox.etherology.components.EtherComponent;
import ru.feytox.etherology.components.FloatComponent;
import ru.feytox.etherology.components.IFloatComponent;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EtherologyComponents implements EntityComponentInitializer, ChunkComponentInitializer {
    public static final ComponentKey<IFloatComponent> ETHER_MAX =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_max"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_REGEN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_regen"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_POINTS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_points"), IFloatComponent.class);
    public static final ComponentKey<CorruptionComponent> CORRUPTION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("corruption"), CorruptionComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ETHER_MAX, player -> new FloatComponent(player, 20), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(ETHER_REGEN, player -> new FloatComponent(player, 0.001F), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(ETHER_POINTS, player -> new EtherComponent(player, 15), RespawnCopyStrategy.LOSSLESS_ONLY);
    }

    @Override
    public void registerChunkComponentFactories(@NotNull ChunkComponentFactoryRegistry registry) {
        registry.register(CORRUPTION, CorruptionComponent::new);
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
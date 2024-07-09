package ru.feytox.etherology.registry.misc;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import ru.feytox.etherology.magic.corruption.CorruptionComponent;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.magic.zones.ZoneComponent;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EtherologyComponents implements EntityComponentInitializer, ChunkComponentInitializer {

    public static final ComponentKey<CorruptionComponent> CORRUPTION =
            ComponentRegistryV3.INSTANCE.getOrCreate(EIdentifier.of("corruption"), CorruptionComponent.class);

    public static final ComponentKey<ZoneComponent> ESSENCE_ZONE =
            ComponentRegistryV3.INSTANCE.getOrCreate(EIdentifier.of("essence_zone"), ZoneComponent.class);

    public static final ComponentKey<EtherComponent> ETHER =
            ComponentRegistryV3.INSTANCE.getOrCreate(EIdentifier.of("ether"), EtherComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, ETHER, EtherComponent::new);

    }

    @Override
    public void registerChunkComponentFactories(@NotNull ChunkComponentFactoryRegistry registry) {
        registry.register(CORRUPTION, CorruptionComponent::new);
        registry.register(ESSENCE_ZONE, ZoneComponent::new);
    }
}
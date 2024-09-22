package ru.feytox.etherology.registry.misc;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import ru.feytox.etherology.gui.teldecore.data.TeldecoreComponent;
import ru.feytox.etherology.gui.teldecore.misc.VisitedComponent;
import ru.feytox.etherology.magic.corruption.CorruptionComponent;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EtherologyComponents implements EntityComponentInitializer, ChunkComponentInitializer {

    public static final ComponentKey<CorruptionComponent> CORRUPTION =
            ComponentRegistryV3.INSTANCE.getOrCreate(EIdentifier.of("corruption"), CorruptionComponent.class);

    public static final ComponentKey<EtherComponent> ETHER =
            ComponentRegistryV3.INSTANCE.getOrCreate(EIdentifier.of("ether"), EtherComponent.class);

    public static final ComponentKey<TeldecoreComponent> TELDECORE =
            ComponentRegistryV3.INSTANCE.getOrCreate(EIdentifier.of("teldecore"), TeldecoreComponent.class);

    public static final ComponentKey<VisitedComponent> VISITED =
            ComponentRegistryV3.INSTANCE.getOrCreate(EIdentifier.of("visited"), VisitedComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, ETHER, EtherComponent::new);
        registry.registerForPlayers(TELDECORE, TeldecoreComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(VISITED, VisitedComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    @Override
    public void registerChunkComponentFactories(@NotNull ChunkComponentFactoryRegistry registry) {
        registry.register(CORRUPTION, CorruptionComponent::new);
    }
}
package ru.feytox.etherology.registry.misc;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.corruption.CorruptionComponent;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.magic.zones.ZoneComponent;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EtherologyComponents implements EntityComponentInitializer, ChunkComponentInitializer, ItemComponentInitializer {

    public static final ComponentKey<CorruptionComponent> CORRUPTION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("corruption"), CorruptionComponent.class);

    public static final ComponentKey<ZoneComponent> ESSENCE_ZONE =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("essence_zone"), ZoneComponent.class);

    public static final ComponentKey<LensComponent> LENS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("lens"), LensComponent.class);

    public static final ComponentKey<StaffComponent> STAFF =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("staff"), StaffComponent.class);

    public static final ComponentKey<EtherComponent> ETHER =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether"), EtherComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, ETHER, EtherComponent::new);

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
}
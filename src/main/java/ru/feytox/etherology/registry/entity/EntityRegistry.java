package ru.feytox.etherology.registry.entity;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.entity.redstoneBlob.RedstoneChargeEntity;
import ru.feytox.etherology.entity.redstoneBlob.RedstoneChargeRenderer;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.function.Consumer;

@UtilityClass
public class EntityRegistry {

    public static final EntityType<RedstoneChargeEntity> REDSTONE_CHARGE = registerType("redstone_charge", SpawnGroup.MISC, RedstoneChargeEntity::new, builder -> builder.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(4).trackedUpdateRate(10));

    public static void registerServerSide() {
    }

    public static void registerClientSide() {
        EntityRendererRegistry.register(REDSTONE_CHARGE, RedstoneChargeRenderer::new);
    }

    private static <T extends Entity> EntityType<T> registerType(String id, SpawnGroup spawnGroup, EntityType.EntityFactory<T> factory, Consumer<FabricEntityTypeBuilder<T>> extraOptions) {
        val builder = FabricEntityTypeBuilder.create(spawnGroup, factory);
        extraOptions.accept(builder);
        return Registry.register(Registries.ENTITY_TYPE, EIdentifier.of(id), builder.build());
    }
}

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
import ru.feytox.etherology.entity.redstoneBlob.RedstoneBlob;
import ru.feytox.etherology.entity.redstoneBlob.RedstoneBlobRenderer;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.function.Consumer;

@UtilityClass
public class EntityRegistry {

    public static final EntityType<RedstoneBlob> REDSTONE_BLOB = registerType("redstone_blob", SpawnGroup.MISC, RedstoneBlob::new, builder -> builder.dimensions(EntityDimensions.fixed(0.5f, 0.5f)));

    public static void registerServerSide() {}

    public static void registerClientSide() {
        // TODO: 21.12.2023 register renderer and test
        EntityRendererRegistry.register(REDSTONE_BLOB, RedstoneBlobRenderer::new);
    }

    private static <T extends Entity> EntityType<T> registerType(String id, SpawnGroup spawnGroup, EntityType.EntityFactory<T> factory, Consumer<FabricEntityTypeBuilder<T>> extraOptions) {
        val builder = FabricEntityTypeBuilder.create(spawnGroup, factory);
        extraOptions.accept(builder);
        return Registry.register(Registries.ENTITY_TYPE, new EIdentifier(id), builder.build());
    }
}

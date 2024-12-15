package ru.feytox.etherology.client.registry;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import ru.feytox.etherology.client.entity.RedstoneChargeRenderer;
import ru.feytox.etherology.registry.entity.EntityRegistry;

@UtilityClass
public class EntityClientRegistry {

    public static void register() {
        EntityRendererRegistry.register(EntityRegistry.REDSTONE_CHARGE, RedstoneChargeRenderer::new);
    }
}

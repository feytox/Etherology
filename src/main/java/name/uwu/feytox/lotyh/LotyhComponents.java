package name.uwu.feytox.lotyh;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import name.uwu.feytox.lotyh.components.FloatComponent;
import name.uwu.feytox.lotyh.components.IBooleanComponent;
import name.uwu.feytox.lotyh.components.IFloatComponent;
import name.uwu.feytox.lotyh.util.LIdentifier;
import name.uwu.feytox.lotyh.components.EtherComponent;

public class LotyhComponents implements EntityComponentInitializer {
    public static final ComponentKey<IFloatComponent> ETHER_MAX =
            ComponentRegistryV3.INSTANCE.getOrCreate(new LIdentifier("ether_max"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_REGEN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new LIdentifier("ether_regen"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_POINTS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new LIdentifier("ether_points"), IFloatComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ETHER_MAX, player -> new FloatComponent(player, 20), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(ETHER_REGEN, player -> new FloatComponent(player, 0.001F), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(ETHER_POINTS, player -> new EtherComponent(player, 15), RespawnCopyStrategy.LOSSLESS_ONLY);
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

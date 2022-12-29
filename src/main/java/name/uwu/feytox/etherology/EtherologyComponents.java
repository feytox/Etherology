package name.uwu.feytox.etherology;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import name.uwu.feytox.etherology.components.EtherComponent;
import name.uwu.feytox.etherology.components.FloatComponent;
import name.uwu.feytox.etherology.components.IFloatComponent;
import name.uwu.feytox.etherology.util.EIdentifier;

public class EtherologyComponents implements EntityComponentInitializer {
    public static final ComponentKey<IFloatComponent> ETHER_MAX =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_max"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_REGEN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_regen"), IFloatComponent.class);
    public static final ComponentKey<IFloatComponent> ETHER_POINTS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new EIdentifier("ether_points"), IFloatComponent.class);

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

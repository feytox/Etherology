package ru.feytox.etherology.world.biome;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import ru.feytox.etherology.util.misc.EIdentifier;
import terrablender.api.Regions;
import terrablender.api.TerraBlenderApi;

public class EtherBiomes implements TerraBlenderApi {

    public static final RegistryKey<Biome> GOLDEN_FOREST = of("golden_forest");

    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new GoldenForestRegion());
    }

    private static RegistryKey<Biome> of(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, new EIdentifier(name));
    }
}

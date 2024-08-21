package ru.feytox.etherology.util.misc;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class GoldenForestGrassModifier extends GrassColorModifierMixin {

    @Override
    public int getModifiedGrassColor(double x, double z, int color) {
        double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
        return d < -0.1 ? 0x4EEC75 : 0x4FE768;
    }
}

@Mixin(BiomeEffects.GrassColorModifier.class)
abstract class GrassColorModifierMixin {

    @Shadow
    public abstract int getModifiedGrassColor(double x, double z, int color);

}

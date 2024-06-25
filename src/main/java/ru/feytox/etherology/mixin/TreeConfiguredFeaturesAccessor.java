package ru.feytox.etherology.mixin;

import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TreeConfiguredFeatures.class)
public interface TreeConfiguredFeaturesAccessor {
    @Invoker
    static TreeFeatureConfig.Builder callSuperBirch() {
        throw new UnsupportedOperationException();
    }
}

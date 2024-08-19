package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.GrassBlock;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.registry.world.WorldGenRegistry;

import java.util.Optional;

@Mixin(GrassBlock.class)
public class GrassBlockMixin {

    @WrapOperation(method = "grow", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;getEntry(Lnet/minecraft/registry/RegistryKey;)Ljava/util/Optional;"))
    private Optional<RegistryEntry.Reference<PlacedFeature>> injectGoldenForestBonemeal(Registry<PlacedFeature> instance, RegistryKey<PlacedFeature> tRegistryKey, Operation<Optional<RegistryEntry.Reference<PlacedFeature>>> original, @Local(argsOnly = true) ServerWorld world, @Local(argsOnly = true) BlockPos pos) {
        RegistryKey<PlacedFeature> featureKey = WorldGenRegistry.getGoldenForestBonemeal(world, pos).orElse(tRegistryKey);
        return original.call(instance, featureKey);
    }
}

package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.family.BlockFamily;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.registry.block.BlockFamilyAccess;

import java.util.List;
import java.util.Map;

@Mixin(BlockStateModelGenerator.BlockTexturePool.class)
public class BlockTexturePoolMixin {

    @ModifyExpressionValue(method = "family", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/family/BlockFamily;getVariants()Ljava/util/Map;"))
    private Map<BlockFamily.Variant, Block> injectVariantsExcluding(Map<BlockFamily.Variant, Block> original, @Local(argsOnly = true) BlockFamily family) {
        if (!(family instanceof BlockFamilyAccess access)) return original;
        List<BlockFamily.Variant> excludedVariants = access.etherology$getExcludedVariants();
        if (excludedVariants == null) return original;

        Map<BlockFamily.Variant, Block> result = new Object2ObjectOpenHashMap<>(original);
        result.entrySet().removeIf(entry -> excludedVariants.contains(entry.getKey()));
        return result;
    }
}

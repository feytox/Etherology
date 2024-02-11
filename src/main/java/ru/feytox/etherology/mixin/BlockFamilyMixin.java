package ru.feytox.etherology.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.family.BlockFamily;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.feytox.etherology.registry.block.BlockFamilyAccess;

import java.util.Arrays;
import java.util.List;

@Mixin(BlockFamily.class)
public class BlockFamilyMixin implements BlockFamilyAccess {

    @Unique
    private List<BlockFamily.Variant> etherology$excludedVariants;

    @Override
    @Nullable
    public List<BlockFamily.Variant> etherology$getExcludedVariants() {
        return etherology$excludedVariants;
    }

    @Override
    public void etherology$addExcludedVariants(BlockFamily.Variant... variants) {
        if (etherology$excludedVariants == null) etherology$excludedVariants = new ObjectArrayList<>();
        etherology$excludedVariants.addAll(Arrays.asList(variants));
    }
}

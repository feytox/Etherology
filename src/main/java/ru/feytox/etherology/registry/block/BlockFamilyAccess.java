package ru.feytox.etherology.registry.block;

import net.minecraft.data.family.BlockFamily;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BlockFamilyAccess {

    @Nullable
    List<BlockFamily.Variant> etherology$getExcludedVariants();

    void etherology$addExcludedVariants(BlockFamily.Variant... variants);
}

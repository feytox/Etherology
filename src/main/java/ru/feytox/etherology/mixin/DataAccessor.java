package ru.feytox.etherology.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.recipe.RawShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RawShapedRecipe.Data.class)
public interface DataAccessor {

    @Accessor
    static MapCodec<RawShapedRecipe.Data> getCODEC() {
        throw new UnsupportedOperationException();
    }
}

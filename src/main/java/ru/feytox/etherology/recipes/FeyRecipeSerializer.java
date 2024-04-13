package ru.feytox.etherology.recipes;

import lombok.Getter;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;

public abstract class FeyRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

    @Getter
    private final Identifier id;
    @Getter(lazy = true)
    private final RecipeType<T> recipeType = createType();

    public FeyRecipeSerializer(String id) {
        this.id = new EIdentifier(id);
    }

    private RecipeType<T> createType() {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
    }
}

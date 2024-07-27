package ru.feytox.etherology.recipes.alchemy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;

@RequiredArgsConstructor
public class AlchemyRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final Ingredient inputItem;
    private final int inputAmount;
    @Nullable
    private AspectContainer inputAspects = null;
    private final ItemStack outputStack;

    public static AlchemyRecipeBuilder create(@NonNull ItemConvertible inputItem, @NonNull Item output) {
        return create(Ingredient.ofItems(inputItem), 1, output, 1);
    }

    public static AlchemyRecipeBuilder create(@NonNull Ingredient inputItem, @NonNull Item output) {
        return create(inputItem, 1, output, 1);
    }

    public static AlchemyRecipeBuilder create(@NonNull Ingredient inputItem, int inputAmount, @NonNull Item output) {
        return create(inputItem, inputAmount, output, 1);
    }

    public static AlchemyRecipeBuilder create(@NonNull Ingredient inputItem, int inputAmount, @NonNull Item output, int outputCount) {
        return new AlchemyRecipeBuilder(inputItem, inputAmount, new ItemStack(output, outputCount));
    }

    public AlchemyRecipeBuilder add(Aspect aspect, int value) {
        if (inputAspects == null) inputAspects = AspectContainer.of(aspect, value);
        else inputAspects = inputAspects.add(AspectContainer.of(aspect, value));
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        Etherology.ELOGGER.warn("Criterion is not yet supported by Brewing Cauldron recipe type.");
        return null;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        Etherology.ELOGGER.warn("Group is not yet supported by Brewing Cauldron recipe type.");
        return null;
    }

    @Override
    public Item getOutputItem() {
        return outputStack.getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        if (inputAspects == null) {
            Etherology.ELOGGER.warn("Input aspects is empty for recipe {}", recipeId);
            inputAspects = new AspectContainer();
        }
        AlchemyRecipe recipe = new AlchemyRecipe(inputItem, inputAmount, inputAspects, outputStack);
        exporter.accept(recipeId, recipe, null);
    }
}

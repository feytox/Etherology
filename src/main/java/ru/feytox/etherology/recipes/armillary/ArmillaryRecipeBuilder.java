package ru.feytox.etherology.recipes.armillary;

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

import java.util.List;

@RequiredArgsConstructor
public class ArmillaryRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final Ingredient centerInput;
    private final List<Aspect> aspects;
    private final float etherPoints;
    private final ItemStack outputStack;

    public static ArmillaryRecipeBuilder create(@NonNull ItemConvertible centerInput, @NonNull ItemConvertible output, float etherPoints, Aspect... aspects) {
        return create(Ingredient.ofItems(centerInput), output, etherPoints, aspects);
    }

    public static ArmillaryRecipeBuilder create(@NonNull Ingredient centerInput, @NonNull ItemConvertible output, float etherPoints, Aspect... aspects) {
        if (aspects.length != 3) throw new IllegalArgumentException("You must provide exactly 3 aspects, found: " + aspects.length);
        return new ArmillaryRecipeBuilder(centerInput, List.of(aspects), etherPoints, output.asItem().getDefaultStack());
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        Etherology.ELOGGER.warn("Criterion is not yet supported by Armillary recipe type.");
        return null;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        Etherology.ELOGGER.warn("Group is not yet supported by Armillary recipe type.");
        return null;
    }

    @Override
    public Item getOutputItem() {
        return outputStack.getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        ArmillaryRecipe recipe = new ArmillaryRecipe(centerInput, aspects, etherPoints, outputStack);
        exporter.accept(recipeId, recipe, null);
    }
}

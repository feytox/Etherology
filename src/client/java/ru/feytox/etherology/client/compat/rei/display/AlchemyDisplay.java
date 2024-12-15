package ru.feytox.etherology.client.compat.rei.display;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.client.compat.rei.misc.AspectPair;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AlchemyDisplay extends BasicDisplay {

    public AlchemyDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Identifier recipeId) {
        super(inputs, outputs, Optional.of(recipeId));
    }

    public static AlchemyDisplay of(RecipeEntry<AlchemyRecipe> entry) {
        AlchemyRecipe recipe = entry.value();
        List<EntryIngredient> inputs = ObjectArrayList.of(EntryIngredient.of(Arrays.stream(recipe.getInputItem().getMatchingStacks())
                .map(stack -> stack.copyWithCount(recipe.getInputAmount())).map(EntryStacks::of).toList()));
        recipe.getInputAspects().getAspects().forEach((aspect, value) -> inputs.add(EntryIngredient.of(AspectPair.entry(aspect, value))));
        List<EntryIngredient> output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
        return new AlchemyDisplay(inputs, output, entry.id());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return EtherREIPlugin.ALCHEMY;
    }
}

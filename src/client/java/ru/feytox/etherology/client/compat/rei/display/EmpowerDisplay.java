package ru.feytox.etherology.client.compat.rei.display;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.RecipeEntry;
import ru.feytox.etherology.client.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.registry.item.EItems;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class EmpowerDisplay extends BasicDisplay {

    private final EmpowerRecipe recipe;

    public EmpowerDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, RecipeEntry<EmpowerRecipe> recipe) {
        super(inputs, outputs, Optional.of(recipe.id()));
        this.recipe = recipe.value();
    }
    
    public static EmpowerDisplay of(RecipeEntry<EmpowerRecipe> entry) {
        EmpowerRecipe recipe = entry.value();
        List<EntryIngredient> inputs = recipe.getPattern().ingredients().stream().map(EntryIngredients::ofIngredient).collect(Collectors.toCollection(ObjectArrayList::new));
        inputs.add(EntryIngredients.of(EItems.PRIMOSHARD_RELLA, recipe.getRellaCount()));
        inputs.add(EntryIngredients.of(EItems.PRIMOSHARD_VIA, recipe.getViaCount()));
        inputs.add(EntryIngredients.of(EItems.PRIMOSHARD_CLOS, recipe.getClosCount()));
        inputs.add(EntryIngredients.of(EItems.PRIMOSHARD_KETA, recipe.getKetaCount()));
        List<EntryIngredient> outputs = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
        return new EmpowerDisplay(inputs, outputs, entry);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return EtherREIPlugin.EMPOWERMENT;
    }
}

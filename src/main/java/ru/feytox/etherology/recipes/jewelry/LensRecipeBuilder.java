package ru.feytox.etherology.recipes.jewelry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;

import java.util.List;

@RequiredArgsConstructor(staticName = "create")
public class LensRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final List<String> pattern = new ObjectArrayList<>();
    @NonNull
    private final Item outputItem;
    private final int etherPoints;

    public LensRecipeBuilder pattern(String patternStr) {
        pattern.add(patternStr);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        Etherology.ELOGGER.warn("Criterion is not yet supported by Lens recipe type.");
        return null;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        Etherology.ELOGGER.warn("Group is not yet supported by Lens recipe type.");
        return null;
    }

    @Override
    public Item getOutputItem() {
        return outputItem;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        AbstractJewelryRecipe.Pattern pattern = AbstractJewelryRecipe.Pattern.create(this.pattern);
        LensRecipe recipe = new LensRecipe(pattern, outputItem, etherPoints);
        exporter.accept(recipeId, recipe, null);
    }
}

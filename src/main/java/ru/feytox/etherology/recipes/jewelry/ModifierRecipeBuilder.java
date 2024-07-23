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
import ru.feytox.etherology.magic.lens.LensModifier;

import java.util.List;

@RequiredArgsConstructor(staticName = "create")
public class ModifierRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final List<String> pattern = new ObjectArrayList<>();
    @NonNull
    private final LensModifier modifier;
    private final int etherPoints;

    public ModifierRecipeBuilder pattern(String patternStr) {
        pattern.add(patternStr);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        Etherology.ELOGGER.warn("Criterion is not yet supported by Lens Modifier recipe type.");
        return null;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        Etherology.ELOGGER.warn("Group is not yet supported by Lens Modifier recipe type.");
        return null;
    }

    @Override @Nullable
    public Item getOutputItem() {
        Etherology.ELOGGER.error("Modifier Recipe type must not use getOutputItem");
        return null;
    }

    @Override
    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, modifier.modifierId());
    }

    @Override
    public void offerTo(RecipeExporter exporter, String recipePath) {
        Identifier pathId = Identifier.of(recipePath);
        if (pathId.equals(modifier.modifierId())) {
            throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
        } else {
            this.offerTo(exporter, pathId);
        }
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        AbstractJewelryRecipe.Pattern pattern = AbstractJewelryRecipe.Pattern.create(this.pattern);
        ModifierRecipe recipe = new ModifierRecipe(pattern, modifier, etherPoints);
        exporter.accept(recipeId, recipe, null);
    }
}

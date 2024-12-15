package ru.feytox.etherology.client.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.compat.emi.EtherEMIPlugin;
import ru.feytox.etherology.client.compat.emi.misc.EmiUtil;
import ru.feytox.etherology.client.compat.emi.misc.FeyEmiRecipe;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.registry.item.EItems;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EmpowerERecipe extends FeyEmiRecipe {

    public EmpowerERecipe(List<EmiIngredient> inputs, List<EmiStack> outputs, Identifier id) {
        super(inputs, outputs, id);
    }

    public static EmpowerERecipe of(RecipeEntry<EmpowerRecipe> entry) {
        EmpowerRecipe recipe = entry.value();
        List<EmiIngredient> inputs = recipe.getPattern().ingredients().stream().map(EmiIngredient::of).collect(Collectors.toCollection(ObjectArrayList::new));
        inputs.add(EmiUtil.ofEmpty(EItems.PRIMOSHARD_RELLA, recipe.getRellaCount()));
        inputs.add(EmiUtil.ofEmpty(EItems.PRIMOSHARD_VIA, recipe.getViaCount()));
        inputs.add(EmiUtil.ofEmpty(EItems.PRIMOSHARD_CLOS, recipe.getClosCount()));
        inputs.add(EmiUtil.ofEmpty(EItems.PRIMOSHARD_KETA, recipe.getKetaCount()));
        List<EmiStack> outputs = Collections.singletonList(EmiStack.of(recipe.getOutput()));
        return new EmpowerERecipe(inputs, outputs, entry.id());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EtherEMIPlugin.EMPOWERMENT;
    }

    @Override
    public int getDisplayWidth() {
        return 122;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 64, 20);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row != 1 && col != 1) continue;
                widgets.addSlot(inputs.get(col + row*3), 3 + col * 18, 3 + row * 18);
            }
        }

        widgets.addSlot(inputs.get(9), 1, 1);
        widgets.addSlot(inputs.get(10), 41, 1);
        widgets.addSlot(inputs.get(11), 1, 41);
        widgets.addSlot(inputs.get(12), 41, 41);

        widgets.addSlot(outputs.getFirst(), 95, 17).large(true).recipeContext(this);
    }
}

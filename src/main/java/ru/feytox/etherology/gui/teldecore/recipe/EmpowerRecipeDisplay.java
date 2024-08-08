package ru.feytox.etherology.gui.teldecore.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

public class EmpowerRecipeDisplay extends AbstractRecipeDisplay<EmpowerRecipe> {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/recipe/empowerment.png");

    public EmpowerRecipeDisplay(EmpowerRecipe recipe, int width, int height) {
        super(recipe, width, height, TEXTURE);
    }

    @Override
    public List<FeySlot> toSlots(float x, float y) {
        List<FeySlot> slots = new ObjectArrayList<>();
        List<Ingredient> input = recipe.getPattern().ingredients();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row != 1 && col != 1) continue;
                slots.add(FeySlot.of(input.get(col+row*3), x+11+col*17, y+11+row*17));
            }
        }

        if (recipe.getRellaCount() > 0) slots.add(FeySlot.of(new ItemStack(EItems.PRIMOSHARD_RELLA, recipe.getRellaCount()), x+10, y+10));
        if (recipe.getViaCount() > 0) slots.add(FeySlot.of(new ItemStack(EItems.PRIMOSHARD_VIA, recipe.getViaCount()), x+46, y+10));
        if (recipe.getClosCount() > 0) slots.add(FeySlot.of(new ItemStack(EItems.PRIMOSHARD_CLOS, recipe.getClosCount()), x+10, y+46));
        if (recipe.getKetaCount() > 0) slots.add(FeySlot.of(new ItemStack(EItems.PRIMOSHARD_KETA, recipe.getKetaCount()), x+46, y+46));


        slots.add(FeySlot.of(recipe.getOutput(), x+95, y+28));

        return slots;
    }
}

package ru.feytox.etherology.gui.teldecore.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

public class SmeltingRecipeDisplay extends AbstractRecipeDisplay<SmeltingRecipe> {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/recipe/furnace.png");

    public SmeltingRecipeDisplay(SmeltingRecipe recipe, int width, int height) {
        super(recipe, width, height, TEXTURE);
    }

    @Override
    public List<FeySlot> toSlots(float x, float y) {
        List<FeySlot> slots = new ObjectArrayList<>();

        slots.add(FeySlot.of(recipe.getIngredients().getFirst(), x+18, y+11));
        slots.add(FeySlot.of(recipe.getResult(null), x+95, y+30));
        
        return slots;
    }
}

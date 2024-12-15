package ru.feytox.etherology.client.gui.teldecore.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.recipes.matrix.MatrixRecipe;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

public class MatrixRecipeDisplay extends AbstractRecipeDisplay<MatrixRecipe> {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/recipe/matrix.png");

    public MatrixRecipeDisplay(MatrixRecipe recipe, int width, int height) {
        super(recipe, width, height, TEXTURE);
    }

    @Override
    public List<FeySlot> toSlots(float x, float y) {
        List<FeySlot> slots = new ObjectArrayList<>();

        slots.add(FeySlot.of(recipe.getCenterInput(), x+28, y+28));
        slots.add(FeySlot.of(recipe.getOutput(), x+95, y+28));

        // 5 27 49
        List<Aspect> aspects = recipe.getAspects();
        for (int i = 0; i < aspects.size(); i++) {
            slots.add(FeySlot.of(aspects.get(i), 1, x+71, y+4+i*22).skipCount());
        }

        return slots;
    }
}

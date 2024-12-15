package ru.feytox.etherology.client.gui.teldecore.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipe;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;
import java.util.Map;

public class AlchemyRecipeDisplay extends AbstractRecipeDisplay<AlchemyRecipe> {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/recipe/alchemy.png");

    public AlchemyRecipeDisplay(AlchemyRecipe recipe, int width, int height) {
        super(recipe, width, height, TEXTURE);
    }

    @Override
    public List<FeySlot> toSlots(float x, float y) {
        List<FeySlot> slots = new ObjectArrayList<>();

        slots.add(FeySlot.of(recipe.getInputItem(), x+37, y+3));
        slots.add(FeySlot.of(recipe.getOutput(), x+95, y+28));

        Map<Aspect, Integer> aspects = recipe.getInputAspects().getAspects();
        float startX = x+46 - aspects.size()*9;
        int i = 0;
        for (Aspect aspect : aspects.keySet()) {
            slots.add(FeySlot.of(aspect, aspects.get(aspect), startX+18*i, y+39));
            i++;
        }

        return slots;
    }
}

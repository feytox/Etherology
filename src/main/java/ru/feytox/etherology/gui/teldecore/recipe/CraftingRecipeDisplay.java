package ru.feytox.etherology.gui.teldecore.recipe;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;

public class CraftingRecipeDisplay extends AbstractRecipeDisplay<CraftingRecipe> {

    private static final Identifier SHAPELESS = EIdentifier.of("textures/gui/teldecore/icon/shapeless.png");
    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/recipe/crafting.png");

    public CraftingRecipeDisplay(CraftingRecipe recipe) {
        super(recipe, TEXTURE);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    public List<FeySlot> toSlots(float x, float y) {
        List<FeySlot> slots = new ObjectArrayList<>();
        List<Ingredient> ingredients = recipe.getIngredients();

        int width = 3;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            width = shapedRecipe.getWidth();
        } else {
            slots.add(FeySlot.drawable(CraftingRecipeDisplay::renderShapelessIcon, x+72, y+44, 12, 11));
        }

        for (int i = 0; i < ingredients.size(); i++) {
            slots.add(FeySlot.of(ingredients.get(i), x+11+17*(i % width), y+11+17*(i / width), 16, 16));
        }

        slots.add(FeySlot.of(recipe.getResult(null), x+95, y+28, 16, 16));

        return slots;
    }

    private static void renderShapelessIcon(DrawContext context, float x, float y, float width, float height) {
        context.push();
        context.translate(x, y, 0);
        context.scale(width / 12f, height / 11f, 1);
        RenderSystem.setShaderTexture(0, SHAPELESS);
        RenderUtils.renderTexture(context, 0, 0, 0, 0, 12, 11,12, 11);
        context.pop();
    }
}

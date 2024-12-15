package ru.feytox.etherology.client.gui.teldecore.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.client.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.jewelry.AbstractJewelryRecipe;
import ru.feytox.etherology.recipes.jewelry.LensRecipe;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipe;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class JewelryRecipeDisplay<T extends AbstractJewelryRecipe> extends AbstractRecipeDisplay<T> {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/recipe/jewelry.png");
    private static final Identifier CRACK = EIdentifier.of("textures/gui/teldecore/recipe/jewelry_crack.png");

    public JewelryRecipeDisplay(T recipe, int width, int height) {
        super(recipe, width, height, TEXTURE);
    }

    protected abstract Ingredient getInput();
    protected abstract Ingredient getOutput();

    @Override
    public List<FeySlot> toSlots(float x, float y) {
        List<FeySlot> slots = new ObjectArrayList<>();

        slots.add(FeySlot.of(getInput(), x+9, y+28));
        slots.add(FeySlot.of(getOutput(), x+95, y+28));
        slots.add(FeySlot.drawable((context, x1, y1, width, height) -> renderGrid(context, recipe.getLensPattern(), x1, y1), x+28, y+8, 56, 56));

        return slots;
    }

    private static void renderGrid(DrawContext context, LensPattern pattern, float x0, float y0) {
        for (int pos = 0; pos < 64; pos++) {
            if (JewelryTableInventory.EMPTY_CELLS.contains(pos)) continue;
            if (pattern.getTextureOffset(pos) == 0) continue;
            float x = x0 + (pos & 0b111) * 7;
            float y = y0 + ((pos >> 3) & 0b111) * 7;

            context.push();
            context.translate(x, y, 0);
            context.drawTexture(CRACK, 0, 0, 0, 0, 7, 7, 7, 7);
            context.pop();
        }
    }

    public static class Lens extends JewelryRecipeDisplay<LensRecipe> {

        public Lens(LensRecipe recipe, int width, int height) {
            super(recipe, width, height);
        }

        @Override
        protected Ingredient getInput() {
            return Ingredient.ofItems(EItems.UNADJUSTED_LENS);
        }

        @Override
        protected Ingredient getOutput() {
            return Ingredient.ofItems(recipe.getOutputItem());
        }
    }

    public static class Modifier extends JewelryRecipeDisplay<ModifierRecipe> {

        public Modifier(ModifierRecipe recipe, int width, int height) {
            super(recipe, width, height);
        }

        @Override
        protected Ingredient getInput() {
            return Ingredient.ofItems(EItems.LENSES);
        }

        @Override
        protected Ingredient getOutput() {
            Stream<ItemStack> result = Arrays.stream(EItems.LENSES).map(Item::getDefaultStack)
                    .peek(stack -> LensComponent.getWrapper(stack)
                            .ifPresent(data -> data.set(recipe.getModifier(), LensComponent::incrementLevel).save()));

            return Ingredient.ofStacks(result);
        }
    }
}

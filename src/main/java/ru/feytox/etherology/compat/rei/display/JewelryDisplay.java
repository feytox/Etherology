package ru.feytox.etherology.compat.rei.display;

import lombok.Getter;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import ru.feytox.etherology.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.recipes.jewelry.AbstractJewelryRecipe;
import ru.feytox.etherology.recipes.jewelry.LensRecipe;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipe;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class JewelryDisplay<T extends AbstractJewelryRecipe> extends BasicDisplay {

    protected final T recipe;

    protected JewelryDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, RecipeEntry<T> entry) {
        super(inputs, outputs);
        recipe = entry.value();
    }

    public static class Lens extends JewelryDisplay<LensRecipe> {

        private Lens(List<EntryIngredient> inputs, List<EntryIngredient> outputs, RecipeEntry<LensRecipe> entry) {
            super(inputs, outputs, entry);
        }

        public static Lens of(RecipeEntry<LensRecipe> entry) {
            List<EntryIngredient> input = List.of(EntryIngredients.of(EItems.UNADJUSTED_LENS));
            List<EntryIngredient> output = List.of(EntryIngredients.of(entry.value().getOutputItem()));
            return new Lens(input, output, entry);
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return EtherREIPlugin.JEWELRY_LENS;
        }
    }

    public static class Modifier extends JewelryDisplay<ModifierRecipe> {

        private Modifier(List<EntryIngredient> inputs, List<EntryIngredient> outputs, RecipeEntry<ModifierRecipe> entry) {
            super(inputs, outputs, entry);
        }

        public static Modifier of(RecipeEntry<ModifierRecipe> entry) {
            LensModifier modifier = entry.value().getModifier();
            List<EntryIngredient> input = List.of(EntryIngredient.of(Arrays.stream(EItems.LENSES).map(EntryStacks::of).toList()));
            List<EntryIngredient> output = List.of(EntryIngredient.of(Arrays.stream(EItems.LENSES).map(item -> {
                ItemStack lensStack = item.getDefaultStack();
                lensStack.apply(ComponentTypes.LENS, LensComponent.EMPTY, component -> component.incrementLevel(modifier));
                return lensStack;
            }).map(EntryStacks::of).toList()));
            return new Modifier(input, output, entry);
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return EtherREIPlugin.JEWELRY_MODIFIER;
        }
    }
}

package ru.feytox.etherology.recipes.jewelry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.magic.lense.LensPattern;
import ru.feytox.etherology.registry.util.EtherologyComponents;

@Getter
@RequiredArgsConstructor
public class JewelryRecipe implements Recipe<JewelryTableInventory> {

    private final LensPattern pattern;
    private final Item outputItem;
    private final Identifier id;

    @Override
    public boolean matches(JewelryTableInventory inventory, World world) {
        ItemStack lensStack = inventory.getStack(0);
        val lens = EtherologyComponents.LENS.get(lensStack);
        return lens.getPattern().equals(pattern);
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    public ItemStack getOutput() {
        return outputItem.getDefaultStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JewelryRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<JewelryRecipe> {
        private Type() {}
        public static final JewelryRecipe.Type INSTANCE = new JewelryRecipe.Type();

        public static final String ID = "jewelry_recipe";
    }
}

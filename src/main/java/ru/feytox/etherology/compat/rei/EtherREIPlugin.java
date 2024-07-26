package ru.feytox.etherology.compat.rei;


import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.block.empowerTable.EmpowerTableScreen;
import ru.feytox.etherology.compat.rei.category.EmpowerCategory;
import ru.feytox.etherology.compat.rei.display.EmpowerDisplay;
import ru.feytox.etherology.compat.rei.filler.StaffCarpetCuttingFiller;
import ru.feytox.etherology.compat.rei.filler.StaffCarpetingFiller;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.registry.block.EBlocks;

public class EtherREIPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<EmpowerDisplay> EMPOWERMENT = id("empowerment");

    private static final CraftingRecipeFiller<?>[] CRAFTING_RECIPE_FILLERS = {
            new StaffCarpetingFiller(), new StaffCarpetCuttingFiller()
    };

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new EmpowerCategory());

        registry.addWorkstations(EMPOWERMENT, EntryStacks.of(EBlocks.EMPOWERMENT_TABLE));

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerCategories(registry);
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(EmpowerRecipe.class, EmpowerRecipeSerializer.INSTANCE.getRecipeType(), EmpowerDisplay::of);

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerDisplays(registry);
        }
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(88, 32, 28, 23), EmpowerTableScreen.class, EMPOWERMENT);
    }

    private static <T extends Display> CategoryIdentifier<T> id(String path) {
        return CategoryIdentifier.of(Etherology.MOD_ID, path);
    }
}

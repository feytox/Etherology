package ru.feytox.etherology.compat.rei.filler;

import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import ru.feytox.etherology.magic.staff.StaffColors;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.recipes.staff.StaffCarpetCuttingRecipe;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StaffCarpetCuttingFiller implements CraftingRecipeFiller<StaffCarpetCuttingRecipe> {

    @Override
    public Collection<Display> apply(RecipeEntry<StaffCarpetCuttingRecipe> recipe) {
        EntryIngredient resultStaff = EntryIngredients.of(ToolItems.STAFF);
        EntryIngredient shears = EntryIngredients.of(Items.SHEARS);

        EntryIngredient staffs = EntryIngredient.of(Arrays.stream(StaffColors.values()).map(color -> {
            ItemStack inputStaff = ToolItems.STAFF.getDefaultStack();
            inputStaff.apply(ComponentTypes.STAFF, StaffComponent.DEFAULT, component -> component.setPartInfo(StaffPartInfo.of(StaffPart.HANDLE, color)));
            return EntryStacks.of(inputStaff);
        }).toList());

        return List.of(new DefaultCustomShapelessDisplay(recipe, List.of(staffs, shears), List.of(resultStaff)));
    }

    @Override
    public Class<StaffCarpetCuttingRecipe> getRecipeClass() {
        return StaffCarpetCuttingRecipe.class;
    }
}

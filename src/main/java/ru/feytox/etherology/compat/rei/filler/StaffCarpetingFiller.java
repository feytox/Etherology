package ru.feytox.etherology.compat.rei.filler;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import ru.feytox.etherology.magic.staff.StaffColors;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.recipes.staff.StaffCarpetingRecipe;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StaffCarpetingFiller implements CraftingRecipeFiller<StaffCarpetingRecipe> {

    @Override
    public Collection<Display> apply(RecipeEntry<StaffCarpetingRecipe> recipe) {
        List<Display> displays = new ObjectArrayList<>();
        EntryIngredient staff = EntryIngredients.of(ToolItems.STAFF);

        Arrays.stream(StaffColors.values()).map(color -> {
            ItemStack resultStaff = ToolItems.STAFF.getDefaultStack();
            resultStaff.apply(ComponentTypes.STAFF, StaffComponent.DEFAULT, component -> component.setPartInfo(StaffPartInfo.of(StaffPart.HANDLE, color)));
            return new DefaultCustomShapelessDisplay(recipe, List.of(staff, EntryIngredients.of(color.getCarpet())), List.of(EntryIngredients.of(resultStaff)));
        }).forEach(displays::add);
        return displays;
    }

    @Override
    public Class<StaffCarpetingRecipe> getRecipeClass() {
        return StaffCarpetingRecipe.class;
    }
}

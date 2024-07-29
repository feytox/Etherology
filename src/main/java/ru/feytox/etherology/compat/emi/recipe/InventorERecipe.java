package ru.feytox.etherology.compat.emi.recipe;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.compat.emi.EtherEMIPlugin;
import ru.feytox.etherology.compat.emi.misc.FeyEmiRecipe;
import ru.feytox.etherology.compat.emi.misc.ListEmiIngredient;
import ru.feytox.etherology.item.PatternTabletItem;
import ru.feytox.etherology.magic.staff.*;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.util.misc.ItemUtils;

import java.util.Arrays;
import java.util.List;

public class InventorERecipe extends FeyEmiRecipe {

    private InventorERecipe(List<EmiIngredient> inputs, List<EmiStack> outputs, Identifier id) {
        super(inputs, outputs, id);
    }

    public static void addRecipes(EmiRegistry registry) {
        EmiStack inputStaff = EmiStack.of(ToolItems.STAFF);
        EmiIngredient metals = EmiIngredient.of(Arrays.stream(StaffMetals.values()).map(StaffMetals::getMetalItem).map(EmiStack::of).toList());

        for (Item tablet : EItems.PATTERN_TABLETS) {
            StaffStyles style = ((PatternTabletItem) tablet).getStaffStyle();
            EmiStack tabletInput = EmiStack.of(tablet);

            for (StaffPart part : StaffPart.values()) {
                if (!part.isStyled()) continue;

                List<EmiStack> output = Arrays.stream(StaffMetals.values()).map(metal -> {
                    ItemStack staffStack = ToolItems.STAFF.getDefaultStack();
                    staffStack.apply(ComponentTypes.STAFF, StaffComponent.DEFAULT, component -> component.setPartInfo(new StaffPartInfo(part, style, metal)));
                    return EmiStack.of(staffStack);
                }).toList();
                registry.addRecipe(new InventorERecipe(List.of(inputStaff, tabletInput, metals), output, ItemUtils.suffixId(ToolItems.STAFF, style.getName() + "_" + part.getName())));
            }
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EtherEMIPlugin.INVENTOR;
    }

    @Override
    public int getDisplayHeight() {
        return 44;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 61, 15);

        for (int i = 0; i < 3; i++) {
            widgets.addSlot(inputs.get(i), 1 + 18*i, 14);
        }

        EmiIngredient ingredient = new ListEmiIngredient(outputs, 1);
        widgets.addSlot(ingredient, 91, 10).large(true).recipeContext(this);
    }
}

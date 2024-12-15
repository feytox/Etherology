package ru.feytox.etherology.client.compat.rei.display;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.client.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.item.PatternTabletItem;
import ru.feytox.etherology.magic.staff.*;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InventorDisplay extends BasicDisplay {

    private InventorDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public static void registerFillers(DisplayRegistry registry) {
        EntryIngredient inputStaff = EntryIngredients.of(ToolItems.STAFF);
        EntryIngredient metals = EntryIngredient.of(Arrays.stream(StaffMetals.values()).map(StaffMetals::getMetalItem).map(EntryStacks::of).toList());

        for (Item tablet : EItems.PATTERN_TABLETS) {
            StaffStyles style = ((PatternTabletItem) tablet).getStaffStyle();
            EntryIngredient tabletInput = EntryIngredients.of(tablet);

            for (StaffPart part : StaffPart.values()) {
                if (!part.isStyled()) continue;

                List<EntryStack<ItemStack>> output = Arrays.stream(StaffMetals.values()).map(metal -> {
                    ItemStack staffStack = ToolItems.STAFF.getDefaultStack();
                    staffStack.apply(ComponentTypes.STAFF, StaffComponent.DEFAULT, component -> component.setPartInfo(new StaffPartInfo(part, style, metal)));
                    return EntryStacks.of(staffStack);
                }).toList();
                registry.add(new InventorDisplay(List.of(inputStaff, tabletInput, metals), Collections.singletonList(EntryIngredient.of(output))));
            }
        }
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return EtherREIPlugin.INVENTOR;
    }
}

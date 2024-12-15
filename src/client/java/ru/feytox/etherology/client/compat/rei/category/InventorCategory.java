package ru.feytox.etherology.client.compat.rei.category;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import ru.feytox.etherology.client.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.client.compat.rei.display.InventorDisplay;
import ru.feytox.etherology.client.compat.rei.misc.EPoint;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.List;

public class InventorCategory implements DisplayCategory<InventorDisplay> {

    @Override
    public CategoryIdentifier<? extends InventorDisplay> getCategoryIdentifier() {
        return EtherREIPlugin.INVENTOR;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(EBlocks.INVENTOR_TABLE.getTranslationKey());
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EBlocks.INVENTOR_TABLE);
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public List<Widget> setupDisplay(InventorDisplay display, Rectangle bounds) {
        EPoint start = new EPoint(bounds.getCenterX() - 58, bounds.getCenterY() - 24);
        List<Widget> widgets = new ObjectArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createArrow(start.add(60, 13)));
        widgets.add(Widgets.createResultSlotBackground(start.add(95, 14)));

        for (int i = 0; i < 3; i++) {
            widgets.add(Widgets.createSlot(start.add(1 + 18*i, 14)).entries(display.getInputEntries().get(i)).markInput());
        }

        widgets.add(Widgets.createSlot(start.add(95, 14)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());

        return widgets;
    }
}

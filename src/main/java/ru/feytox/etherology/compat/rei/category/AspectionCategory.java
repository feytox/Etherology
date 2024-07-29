package ru.feytox.etherology.compat.rei.category;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import ru.feytox.etherology.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.compat.rei.display.AspectionDisplay;
import ru.feytox.etherology.compat.rei.misc.EPoint;
import ru.feytox.etherology.registry.item.ToolItems;

import java.util.List;

public class AspectionCategory implements DisplayCategory<AspectionDisplay> {

    @Override
    public CategoryIdentifier<? extends AspectionDisplay> getCategoryIdentifier() {
        return EtherREIPlugin.ASPECTION;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("gui.etherology.aspects");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ToolItems.OCULUS);
    }

    @Override
    public int getDisplayWidth(AspectionDisplay display) {
        int size = display.getOutputEntries().size();
        return Math.max(150, 150 + (size - 4) * 18);
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    @Override
    public List<Widget> setupDisplay(AspectionDisplay display, Rectangle bounds) {
        EPoint start = new EPoint(bounds.getCenterX() - (getDisplayWidth(display) / 2 - 17), bounds.getCenterY() - 21);
        List<Widget> widgets = new ObjectArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createArrow(start.add(26, 13)));
        widgets.add(Widgets.createSlot(start.add(1, 14)).entries(display.getInputEntries().getFirst()).markInput());

        List<EntryIngredient> outputEntries = display.getOutputEntries();
        for (int i = 0; i < outputEntries.size(); i++) {
            widgets.add(Widgets.createSlot(start.add(56 + i*18, 14)).entries(outputEntries.get(i)).markOutput());
        }

        return widgets;
    }
}

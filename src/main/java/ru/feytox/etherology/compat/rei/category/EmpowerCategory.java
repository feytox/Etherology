package ru.feytox.etherology.compat.rei.category;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import ru.feytox.etherology.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.compat.rei.display.EmpowerDisplay;
import ru.feytox.etherology.compat.rei.misc.EPoint;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.List;

public class EmpowerCategory implements DisplayCategory<EmpowerDisplay> {

    @Override
    public CategoryIdentifier<? extends EmpowerDisplay> getCategoryIdentifier() {
        return EtherREIPlugin.EMPOWERMENT;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.etherology.empowerment_table.title");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EBlocks.EMPOWERMENT_TABLE);
    }

    @Override
    public int getDisplayHeight() {
        return 70;
    }

    @Override
    public List<Widget> setupDisplay(EmpowerDisplay display, Rectangle bounds) {
        EPoint start = new EPoint(bounds.getCenterX() - 58, bounds.getCenterY() - 29);
        List<Widget> widgets = new ObjectArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createArrow(start.add(60, 18)));
        widgets.add(Widgets.createResultSlotBackground(start.add(95, 19)));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row != 1 && col != 1) continue;
                widgets.add(Widgets.createSlot(start.add(3 + col * 18, 3 + row * 18)).entries(display.getInputEntries().get(col + row*3)).markInput());
            }
        }

        widgets.add(Widgets.createSlot(start.add(1, 1)).entries(display.getInputEntries().get(9)).markInput());
        widgets.add(Widgets.createSlot(start.add(41, 1)).entries(display.getInputEntries().get(10)).markInput());
        widgets.add(Widgets.createSlot(start.add(1, 41)).entries(display.getInputEntries().get(11)).markInput());
        widgets.add(Widgets.createSlot(start.add(41, 41)).entries(display.getInputEntries().get(12)).markInput());

        widgets.add(Widgets.createSlot(start.add(95, 19)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());

        return widgets;
    }
}

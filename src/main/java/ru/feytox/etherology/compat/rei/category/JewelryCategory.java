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
import ru.feytox.etherology.block.jewelryTable.JewelryTableScreen;
import ru.feytox.etherology.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.compat.rei.display.JewelryDisplay;
import ru.feytox.etherology.compat.rei.misc.EPoint;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.List;

public abstract class JewelryCategory implements DisplayCategory<JewelryDisplay<?>> {

    @Override
    public Text getTitle() {
        return Text.translatable(EBlocks.JEWELRY_TABLE.getTranslationKey());
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EBlocks.JEWELRY_TABLE);
    }

    @Override
    public int getDisplayWidth(JewelryDisplay<?> display) {
        return 200;
    }

    @Override
    public int getDisplayHeight() {
        return 120;
    }

    @Override
    public List<Widget> setupDisplay(JewelryDisplay<?> display, Rectangle bounds) {
        LensPattern pattern = display.getRecipe().getPattern().pattern();
        EPoint start = new EPoint(bounds.getCenterX() - 90, bounds.getCenterY() - 56);
        List<Widget> widgets = new ObjectArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) ->
                JewelryTableScreen.renderGrid(graphics, pattern, start.x+25, start.y+8)));

        widgets.add(Widgets.createArrow(start.add(125, 47)));
        widgets.add(Widgets.createResultSlotBackground(start.add(160, 48)));

        widgets.add(Widgets.createSlot(start.add(1, 48)).entries(display.getInputEntries().getFirst()).markInput());
        widgets.add(Widgets.createSlot(start.add(160, 48)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());

        return widgets;
    }

    public static class Lens extends JewelryCategory {

        @Override
        public CategoryIdentifier<? extends JewelryDisplay<?>> getCategoryIdentifier() {
            return EtherREIPlugin.JEWELRY_LENS;
        }
    }

    public static class Modifier extends JewelryCategory {

        @Override
        public CategoryIdentifier<? extends JewelryDisplay<?>> getCategoryIdentifier() {
            return EtherREIPlugin.JEWELRY_MODIFIER;
        }
    }
}

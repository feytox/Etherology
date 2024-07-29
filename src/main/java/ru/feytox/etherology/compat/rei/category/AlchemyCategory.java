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
import ru.feytox.etherology.compat.rei.display.AlchemyDisplay;
import ru.feytox.etherology.compat.rei.misc.EPoint;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.List;

public class AlchemyCategory implements DisplayCategory<AlchemyDisplay> {

    @Override
    public CategoryIdentifier<? extends AlchemyDisplay> getCategoryIdentifier() {
        return EtherREIPlugin.ALCHEMY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(EBlocks.BREWING_CAULDRON.getTranslationKey());
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EBlocks.BREWING_CAULDRON);
    }

    @Override
    public int getDisplayWidth(AlchemyDisplay display) {
        int size = display.getInputEntries().size();
        return Math.max(168, 168 + (size - 4) * 18);
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public List<Widget> setupDisplay(AlchemyDisplay display, Rectangle bounds) {
        EPoint start = new EPoint(bounds.getCenterX() - (getDisplayWidth(display) / 2 - 17), bounds.getCenterY() - 24);
        List<Widget> widgets = new ObjectArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int dx = 1;
        List<EntryIngredient> inputs = display.getInputEntries();
        for (int i = 0; i < inputs.size(); i++) {
            widgets.add(Widgets.createSlot(start.add(dx, 14)).entries(inputs.get(i)).markInput());
            if (i == 0) dx += 9;
            dx += 18;
        }

        widgets.add(Widgets.createArrow(start.add(dx+4, 13)));
        widgets.add(Widgets.createResultSlotBackground(start.add(dx+39, 14)));
        widgets.add(Widgets.createSlot(start.add(dx+39, 14)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());

        return widgets;
    }
}

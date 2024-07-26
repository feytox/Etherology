package ru.feytox.etherology.compat.rei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.block.jewelryTable.JewelryTableScreen;
import ru.feytox.etherology.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.compat.rei.display.JewelryDisplay;
import ru.feytox.etherology.compat.rei.misc.EPoint;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.misc.RenderUtils;

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

        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> createGrid(graphics, pattern, start.add(25, 8))));

        widgets.add(Widgets.createArrow(start.add(125, 47)));
        widgets.add(Widgets.createResultSlotBackground(start.add(160, 48)));

        widgets.add(Widgets.createSlot(start.add(1, 48)).entries(display.getInputEntries().getFirst()).markInput());
        widgets.add(Widgets.createSlot(start.add(160, 48)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());

        return widgets;
    }

    // TODO: 26.07.2024 consider not using same code twice
    /**
     * @see ru.feytox.etherology.block.jewelryTable.JewelryTableScreen#renderButtons(DrawContext, int, int, int, int)
     */
    private void createGrid(DrawContext context, LensPattern pattern, Point point) {
        context.push();

        for (int pos = 0; pos < 64; pos++) {
            if (JewelryTableInventory.EMPTY_CELLS.contains(pos)) continue;
            int x = point.x + (pos & 0b111) * 12;
            int y = point.y + ((pos >> 3) & 0b111) * 12;
            int offset = pattern.getTextureOffset(pos);

            RenderSystem.setShaderTexture(0, JewelryTableScreen.TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            RenderUtils.renderTexture(context, x, y, 176 + 12 * offset, 0, 12, 12, 12, 12, 256, 256);
        }

        context.pop();
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

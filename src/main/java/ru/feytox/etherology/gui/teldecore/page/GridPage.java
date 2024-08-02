package ru.feytox.etherology.gui.teldecore.page;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;
import ru.feytox.etherology.gui.teldecore.data.ChapterGrid;

import java.util.List;
import java.util.function.Function;

public class GridPage extends EmptyPage {

    private final List<ChapterButton> buttons;
    private float deltaY = 0.0f;

    public GridPage(TeldecoreScreen parent, ChapterGrid grid, Function<Identifier, ItemStack> idToIcon, boolean isLeft) {
        super(parent, isLeft);
        this.buttons = grid.toButtons(parent, idToIcon, getPageX() + PAGE_WIDTH/2f, getPageY()+19, 39f);
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {
        context.enableScissor((int) (pageX+4), (int) (pageY+4), (int) (pageX+PAGE_WIDTH-4), (int) (pageY+PAGE_HEIGHT-4));

        buttons.forEach(button -> button.render(context, mouseX, mouseY, delta));
        context.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ChapterButton chapterButton : buttons) {
            if (chapterButton.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0.0f) return false;

        deltaY += (float) (verticalAmount * 10f);
        buttons.forEach(button -> button.move(getPageX() + PAGE_WIDTH/2f, getPageY()+19+deltaY));
        return true;
    }
}

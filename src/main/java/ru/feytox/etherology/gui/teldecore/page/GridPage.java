package ru.feytox.etherology.gui.teldecore.page;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.data.ChapterGrid;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;
import java.util.function.Function;

public class GridPage extends EmptyPage {

    private final List<Pair<Vec2f, Vec2f>> lines;
    private final List<ChapterButton> buttons;
    private final float maxY;
    private float deltaY = 0.0f;

    public GridPage(TeldecoreScreen parent, ChapterGrid grid, Function<Identifier, Chapter> idToIcon, boolean isLeft) {
        super(parent, isLeft);
        this.buttons = grid.toButtons(parent, idToIcon, getPageX() + PAGE_WIDTH/2f, getPageY()+19, 32f);
        this.maxY = Math.max(0f, this.buttons.stream().map(ChapterButton::getDy).max(Float::compare).orElse(0f) - PAGE_HEIGHT+52f);
        this.lines = grid.toLines(32f);
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {
        context.enableScissor((int) (pageX+4), (int) (pageY+4), (int) (pageX+PAGE_WIDTH-4), (int) (pageY+PAGE_HEIGHT-4));
        renderLines(context, pageX, pageY);
        buttons.forEach(button -> button.render(context, mouseX, mouseY, delta));
        context.disableScissor();

        buttons.forEach(button -> button.renderTooltip(context, mouseX, mouseY));
    }

    private void renderLines(DrawContext context, float pageX, float pageY) {
        float rootX = pageX + PAGE_WIDTH / 2f;
        float rootY = pageY + 19 + deltaY;
        lines.forEach(line -> {
            Vec2f start = line.first();
            Vec2f end = line.second();
            RenderUtils.drawStraightLine(context, start.x+rootX, start.y+rootY, end.x+rootX, end.y+rootY, 2, 0xFFAE9C89);
        });
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

        deltaY = Math.clamp(deltaY + (float) (verticalAmount * 10f), -maxY, 0);
        buttons.forEach(button -> button.move(getPageX() + PAGE_WIDTH/2f, getPageY()+19+deltaY));
        return true;
    }
}

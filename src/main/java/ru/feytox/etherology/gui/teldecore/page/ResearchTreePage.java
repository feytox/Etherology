package ru.feytox.etherology.gui.teldecore.page;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;
import ru.feytox.etherology.gui.teldecore.button.SliderButton;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.data.ResearchTree;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ResearchTreePage extends AbstractPage {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/page/research.png");
    public static final int SLIDER_LENGTH = 179;

    private final List<Pair<Vec2f, Vec2f>> lines;
    private final List<ChapterButton> buttons;
    private final SliderButton slider;
    private final float maxY;
    private float deltaY = 0.0f;

    public ResearchTreePage(TeldecoreScreen parent, ResearchTree grid, Function<Identifier, Chapter> idToChapter, Predicate<Identifier> chapterCheck, boolean isLeft) {
        super(parent, TEXTURE, isLeft, 10, 186);
        this.buttons = grid.toButtons(parent, idToChapter, chapterCheck, getPageX() + PAGE_WIDTH/2f, getPageY()+19, 32f);
        this.maxY = Math.max(0f, this.buttons.stream().map(ChapterButton::getDy).max(Float::compare).orElse(0f) - PAGE_HEIGHT+52f);
        this.lines = grid.toLines(chapterCheck, 32f);
        this.slider = new SliderButton(parent, getPageX()+(isLeft ? 1f : 131f), getPageY()+2, maxY != 0, dy -> {
            deltaY = -maxY * dy / SLIDER_LENGTH;
            updateButtonsPos();
        });
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {
        context.enableScissor((int) (pageX+4), (int) (pageY+4), (int) (pageX+PAGE_WIDTH-4), (int) (pageY+PAGE_HEIGHT-4));
        renderLines(context, pageX, pageY);
        buttons.forEach(button -> button.render(context, mouseX, mouseY, delta));
        context.disableScissor();

        buttons.forEach(button -> button.renderTooltip(context, mouseX, mouseY));
        slider.render(context, mouseX, mouseY, delta);
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
        if (slider.mouseClicked(mouseX, mouseY, button)) return true;
        for (ChapterButton chapterButton : buttons) {
            if (chapterButton.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0.0f) return false;

        deltaY = Math.clamp(deltaY + (float) (verticalAmount * 10f), -maxY, 0);
        if (maxY != 0) slider.setDeltaY(-deltaY * SLIDER_LENGTH / maxY);
        updateButtonsPos();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return slider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return slider.mouseReleased(mouseX, mouseY, button);
    }

    private void updateButtonsPos() {
        buttons.forEach(button -> button.move(getPageX() + PAGE_WIDTH/2f, getPageY()+19+deltaY));
    }
}

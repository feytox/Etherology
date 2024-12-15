package ru.feytox.etherology.client.gui.teldecore.page;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Math;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.client.gui.teldecore.button.ChapterButton;
import ru.feytox.etherology.client.gui.teldecore.button.SliderButton;
import ru.feytox.etherology.client.util.RenderUtils;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.data.ResearchTree;
import ru.feytox.etherology.gui.teldecore.data.TeldecoreComponent;
import ru.feytox.etherology.gui.teldecore.misc.TreeLine;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ResearchTreePage extends AbstractPage {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/page/research.png");
    public static final int SLIDER_LENGTH = 179;
    private static final int LINE_COLOR = 0xFFAE9C89;

    private final List<TreeLine> lines;
    private final List<ChapterButton> buttons;
    private final SliderButton slider;
    private final float maxY;
    private float deltaY = 0.0f;

    public ResearchTreePage(TeldecoreScreen parent, TeldecoreComponent data, ResearchTree grid, Function<Identifier, Chapter> idToChapter, boolean isLeft) {
        super(parent, TEXTURE, isLeft, 10, 186);
        this.buttons = toButtons(grid, parent, data, idToChapter, getPageX() + PAGE_WIDTH/2f, getPageY()+19, 36f);
        this.maxY = Math.max(0f, this.buttons.stream().map(ChapterButton::getDy).max(Float::compare).orElse(0f) - PAGE_HEIGHT+52f);
        this.lines = toLines(grid, data, idToChapter, 36f);
        this.slider = new SliderButton(parent, getPageX()+(isLeft ? 1f : 131f), getPageY()+2, maxY != 0, dy -> {
            deltaY = -maxY * dy / SLIDER_LENGTH;
            updateButtonsPos();
        });
    }

    public static List<ChapterButton> toButtons(ResearchTree tree, TeldecoreScreen parent, TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, float rootX, float rootY, float scale) {
        return tree.getChapterInfos().stream().map(info -> toButton(info, parent, data, idToChapter, rootX, rootY, scale))
                .filter(Optional::isPresent).map(Optional::get).toList();
    }

    public static List<TreeLine> toLines(ResearchTree tree, TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, float scale) {
        return tree.getChapterInfos().stream().map(info -> info.toLines(data, idToChapter, tree.getInfoMap(), scale))
                .filter(Optional::isPresent).flatMap(Optional::get)
                .filter(line -> !line.start().equals(line.end())).toList();
    }

    private static Optional<ChapterButton> toButton(ResearchTree.ChapterInfo chapterInfo, TeldecoreScreen parent, TeldecoreComponent data, Function<Identifier, Chapter> idToChapter, float rootX, float rootY, float scale) {
        var chapter = idToChapter.apply(chapterInfo.id());
        if (chapter == null || !chapter.isAvailable(data)) return Optional.empty();

        var texture = chapter.type().getTexture();
        var icon = Registries.ITEM.get(chapter.icon()).getDefaultStack();
        var title = Text.translatable(chapter.titleKey()).formatted(Formatting.WHITE);
        var desc = Text.translatable(chapter.descKey()).formatted(Formatting.GRAY);
        var wasOpened = data.wasOpened(chapterInfo.id());
        var isSubTab = chapter.subTab().isPresent();
        var glowing = chapter.quest().isPresent() && !data.isCompleted(chapterInfo.id());
        var target = chapter.subTab().orElse(chapterInfo.id());

        return Optional.of(new ChapterButton(parent, texture, target, icon, List.of(title, desc), wasOpened, isSubTab, glowing, rootX, rootY, chapterInfo.x() *scale, chapterInfo.y() *scale));
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {
        context.enableScissor((int) (pageX+4), (int) (pageY+4), (int) (pageX+PAGE_WIDTH-4), (int) (pageY+PAGE_HEIGHT-4));
        renderLines(context, pageX, pageY);
        renderChapterButtons(context, mouseX, mouseY, delta);
        context.disableScissor();

        buttons.forEach(button -> button.renderTooltip(context, mouseX, mouseY));
        slider.render(context, mouseX, mouseY, delta);
    }

    private void renderChapterButtons(DrawContext context, int mouseX, int mouseY, float delta) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return;

        var progress = MathHelper.PI * (world.getTime() + delta) / 40f;
        var tint = 0.25f * (Math.sin(2f * progress) + 3);

        buttons.forEach(button -> button.renderTinted(context, mouseX, mouseY, delta, tint));
    }

    private void renderLines(DrawContext context, float pageX, float pageY) {
        var rootX = pageX + PAGE_WIDTH / 2f;
        var rootY = pageY + 19 + deltaY;
        lines.forEach(line -> {
            var start = line.start();
            var end = line.end();
            RenderUtils.drawStraightLine(context, start.x+rootX, start.y+rootY, end.x+rootX, end.y+rootY, 2, LINE_COLOR);
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (slider.mouseClicked(mouseX, mouseY, button)) return true;
        for (var chapterButton : buttons) {
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

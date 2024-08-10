package ru.feytox.etherology.gui.teldecore.page;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.button.QuestButton;
import ru.feytox.etherology.gui.teldecore.data.Quest;
import ru.feytox.etherology.gui.teldecore.misc.FeyIngredient;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.gui.teldecore.misc.FocusedIngredientProvider;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;
import java.util.stream.IntStream;

public class QuestPage extends AbstractPage implements FocusedIngredientProvider {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/page/quest.png");

    private final List<FeySlot> tasks;
    private final OrderedText title;
    private final Quest quest;
    private final Identifier chapterId;

    public QuestPage(TeldecoreScreen parent, Quest quest, Identifier chapterId, boolean isLeft) {
        super(parent, TEXTURE, isLeft, 31, 145);
        this.tasks = IntStream.range(0, quest.tasks().size())
                .mapToObj(i -> quest.tasks().get(i).toSlot(getPageX()+i*20+(isLeft ? 8 : 156), getPageY()+165, 16, 16))
                .toList();
        this.title = Text.translatable(quest.titleKey()).asOrderedText();
        this.quest = quest;
        this.chapterId = chapterId;
    }

    @Override
    public void initContent() {
        super.initContent();
        addDrawableChild(new QuestButton(parent, quest, chapterId, getPageX(), getPageY(), 112+(isLeft() ? 0 : 5), 165));
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {
        float titleX = pageX + (PAGE_WIDTH - textRenderer.getWidth(title)) / 2f;
        float titleY = pageY + 8;
        textRenderer.draw(title, titleX, titleY, 0x70523D, false, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);

        tasks.forEach(slot -> slot.render(context, mouseX, mouseY, delta));
        tasks.forEach(slot -> {
            if (slot.hasTooltip() && slot.isMouseOver(mouseX, mouseY)) slot.renderTooltip(context, mouseX, mouseY);
        });
    }

    @Override @Nullable
    public FeyIngredient getFocusedIngredient(int mouseX, int mouseY) {
        return tasks.stream().filter(FeySlot::canBeFocused)
                .filter(slot -> slot.isMouseOver(mouseX, mouseY)).findAny().orElse(null);
    }
}

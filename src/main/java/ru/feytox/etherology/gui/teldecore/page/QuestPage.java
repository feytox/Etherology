package ru.feytox.etherology.gui.teldecore.page;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.DrawContext;
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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class QuestPage extends TitlePage implements FocusedIngredientProvider {

    private static final Identifier CAP = EIdentifier.of("textures/gui/teldecore/quest/quest_cap.png");

    private final List<FeySlot> tasks;
    private final Quest quest;
    private final Identifier chapterId;

    public QuestPage(TeldecoreScreen parent, Quest quest, Identifier chapterId, boolean isLeft) {
        super(parent, Text.translatable(quest.titleKey()), isLeft, false);
        this.quest = quest;
        this.chapterId = chapterId;

        Map<TaskType, Integer> typesCount = new EnumMap<>(TaskType.class);
        this.tasks = new ObjectArrayList<>();
        quest.tasks().forEach(task -> {
            int i = typesCount.getOrDefault(task.getTaskType(), 0);
            FeySlot slot = task.toSlot(getPageX() + i * 20 + (isLeft ? 24 : 170), getPageY() + 146 + task.getTaskType().getDy(), 16, 16);
            typesCount.put(task.getTaskType(), i+1);
            tasks.add(slot);
        });
    }

    @Override
    public void initContent() {
        super.initContent();
        addDrawableChild(new QuestButton(parent, quest, chapterId, getPageX(), getPageY(), (PAGE_WIDTH - QuestButton.WIDTH - 9) / 2f, PAGE_HEIGHT - 65));
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {
        super.renderPage(context, pageX, pageY, mouseX, mouseY, delta);
        renderQuestBackground(context, pageX, pageY);

        tasks.forEach(slot -> slot.render(context, mouseX, mouseY, delta));
        tasks.forEach(slot -> {
            if (slot.hasTooltip() && slot.isMouseOver(mouseX, mouseY)) slot.renderTooltip(context, mouseX, mouseY);
        });
    }

    private void renderQuestBackground(DrawContext context, float pageX, float pageY) {
        context.push();

        context.translate(pageX + 8, pageY + PAGE_HEIGHT - 60, 0);
        context.drawTexture(CAP, 0, 0, 0, 0, 118, 1, 118, 1);

        context.translate(0, 12, 0);
        for (TaskType taskType : TaskType.values()) {
            taskType.render(context);
        }

        context.pop();
    }

    @Override
    @Nullable
    public FeyIngredient getFocusedIngredient(int mouseX, int mouseY) {
        return tasks.stream().filter(FeySlot::canBeFocused)
                .filter(slot -> slot.isMouseOver(mouseX, mouseY)).findAny().orElse(null);
    }
}

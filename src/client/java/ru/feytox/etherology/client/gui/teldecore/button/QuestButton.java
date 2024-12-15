package ru.feytox.etherology.client.gui.teldecore.button;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.data.Quest;
import ru.feytox.etherology.network.interaction.QuestCompleteC2S;
import ru.feytox.etherology.util.misc.EIdentifier;

public class QuestButton extends AbstractButton {

    public static final int WIDTH = 58;
    private static final Identifier BUTTON = EIdentifier.of("textures/gui/teldecore/quest/complete_button.png");
    private static final Identifier HOVER_BUTTON = EIdentifier.of("textures/gui/teldecore/quest/complete_button_hover.png");
    private final Identifier chapterId;
    private final Text buttonText;

    public QuestButton(TeldecoreScreen parent, Quest quest, Identifier chapterId, float pageX, float pageY, float dx, float dy) {
        super(parent, BUTTON, HOVER_BUTTON, pageX, pageY, dx, dy, WIDTH, 13);
        this.active = quest.isCompleted(MinecraftClient.getInstance().player);
        this.chapterId = chapterId;
        this.buttonText = Text.translatable("gui.etherology.complete");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!active) return;
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        float x = baseX + (WIDTH - textRenderer.getWidth(buttonText)) / 2f;
        TeldecoreScreen.renderText(context, textRenderer, buttonText, x, baseY+3);
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        parent.executeOnPlayer(player -> player.playSound(active ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO));
        ClientPlayNetworking.send(new QuestCompleteC2S(chapterId));

        // client-side quest completing
        parent.getData().ifPresent(data -> data.addCompletedQuest(chapterId));
        parent.clearAndInit();

        return active;
    }
}

package ru.feytox.etherology.gui.teldecore.button;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.data.Quest;
import ru.feytox.etherology.network.interaction.QuestCompleteC2S;
import ru.feytox.etherology.util.misc.EIdentifier;

public class QuestButton extends AbstractButton {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/icon/quest_complete.png");
    private final Identifier chapterId;

    public QuestButton(TeldecoreScreen parent, Quest quest, Identifier chapterId, float pageX, float pageY, float dx, float dy) {
        super(parent, TEXTURE, null, pageX, pageY, dx, dy, 16, 16);
        this.active = quest.isCompleted(MinecraftClient.getInstance().player);
        this.chapterId = chapterId;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!active) return;
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        parent.executeOnPlayer(player -> player.playSound(active ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO));
        new QuestCompleteC2S(chapterId).sendToServer();
        return active;
    }
}

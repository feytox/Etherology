package ru.feytox.etherology.gui.teldecore.button;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

public class ChapterButton extends AbstractButton {

    private static final Identifier MARK = EIdentifier.of("textures/gui/teldecore/icon/chapter_mark.png");

    private final Identifier target;
    private final ItemStack icon;
    private final List<Text> tooltip;
    private final float dx;
    @Getter
    private final float dy;
    private final boolean wasOpened;
    private final boolean isSubTab;

    public ChapterButton(TeldecoreScreen parent, Identifier texture, Identifier target, ItemStack icon, List<Text> tooltip, boolean wasOpened, boolean isSubTab, float rootX, float rootY, float dx, float dy) {
        super(parent, texture, null, rootX, rootY, dx-16, dy-16, 32, 32);
        this.target = target;
        this.icon = icon;
        this.dx = dx-13;
        this.dy = dy-13;
        this.tooltip = tooltip;
        this.wasOpened = wasOpened;
        this.isSubTab = isSubTab;
    }

    public void move(float rootX, float rootY) {
        this.baseX = rootX + dx;
        this.baseY = rootY + dy;
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        float x = baseX + width / 2f - 8.0f;
        float y = baseY + height / 2f - 8.0f;
        context.push();
        context.translate(x, y, 0);
        context.drawItem(icon, 0, 0);
        context.pop();
        if (wasOpened) return;

        context.push();
        context.translate(baseX+width-8, baseY-2, 0);
        context.drawTexture(MARK, 0, 0, 0, 0, 4, 11, 4, 11);
        context.pop();
    }

    public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        if (!isMouseOver(mouseX, mouseY)) return;
        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        return dataAction("Failed to handle click on chapter %s button".formatted(target.toString()), data -> {
            if (isSubTab) data.switchTab(target);
            else data.setSelectedChapter(target);
            data.addOpened(target);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        });
    }
}

package ru.feytox.etherology.gui.teldecore.button;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;

import java.util.List;

public class ChapterButton extends AbstractButton {

    private final Identifier target;
    private final ItemStack icon;
    private final List<Text> tooltip;
    private final float dx;
    @Getter
    private final float dy;

    public ChapterButton(TeldecoreScreen parent, Identifier texture, Identifier target, ItemStack icon, List<Text> tooltip, float rootX, float rootY, float dx, float dy) {
        super(parent, texture, null, rootX, rootY, dx-13, dy-13, 26, 26);
        this.target = target;
        this.icon = icon;
        this.dx = dx-13;
        this.dy = dy-13;
        this.tooltip = tooltip;
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
    }

    public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        if (!isMouseOver(mouseX, mouseY)) return;
        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean onClick(int button) {
        return dataAction("Failed to handle click on chapter %s button".formatted(target.toString()), data -> {
            data.setSelectedChapter(target);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        });
    }
}

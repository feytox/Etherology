package ru.feytox.etherology.gui.teldecore.button;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

public class ChapterButton extends AbstractButton {

    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/teldecore/icon/chapter_0.png");

    private final Identifier target;
    private final ItemStack icon;

    public ChapterButton(TeldecoreScreen parent, Identifier target, ItemStack icon, float dx, float dy) {
        super(parent, TEXTURE, null, dx, dy, 26, 26);
        this.target = target;
        this.icon = icon;
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        int x = (int) (baseX + width / 2f - 8.0f);
        int y = (int) (baseY + height / 2f - 8.0f);
        context.drawItem(icon, x, y);
    }

    @Override
    public boolean onClick(int button) {
        return dataAction("Failed to handle click on chapter %s button".formatted(target.toString()), data -> {
            data.setSelected(target);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        });
    }
}

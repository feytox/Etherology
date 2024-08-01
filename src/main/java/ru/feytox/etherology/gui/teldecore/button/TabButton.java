package ru.feytox.etherology.gui.teldecore.button;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.data.Tab;
import ru.feytox.etherology.util.misc.EIdentifier;

public class TabButton extends AbstractButton {

    private static final Identifier TAB = EIdentifier.of("textures/gui/teldecore/icon/tab_0.png");
    private static final Identifier HOVERED_TAB = EIdentifier.of("textures/gui/teldecore/icon/tab_1.png");

    private final Identifier target;
    private final ItemStack icon;

    private TabButton(TeldecoreScreen parent, Identifier target, ItemStack icon, float dx, float dy) {
        super(parent, TAB, HOVERED_TAB, dx, dy, 30, 25);
        this.target = target;
        this.icon = icon;
    }

    public static TabButton of(TeldecoreScreen parent, Identifier tabId, Tab tab, float dx, float dy) {
        return new TabButton(parent, tabId, Registries.ITEM.get(tab.getIcon()).getDefaultStack(), dx, dy);
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        int x = (int) (baseX + 13 - (hovered ? 1 : 0));
        int y = (int) (baseY + 2);
        context.drawItem(icon, x, y);
    }

    @Override
    public boolean onClick(int button) {
        return dataAction("Failed to handle click on tab %s button".formatted(target.toString()), data -> {
            data.switchTab(target);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        });
    }
}

package ru.feytox.etherology.gui.teldecore.button;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.data.Tab;
import ru.feytox.etherology.util.misc.EIdentifier;

public class SelectedTabButton extends AbstractButton {

    private static final Identifier SELECTED_TAB = EIdentifier.of("textures/gui/teldecore/icon/tab_2.png");

    private final ItemStack icon;

    private SelectedTabButton(TeldecoreScreen parent, ItemStack icon, float dx, float dy) {
        super(parent, SELECTED_TAB, null, dx, dy, 40, 25);
        this.icon = icon;
    }

    public static SelectedTabButton of(TeldecoreScreen parent, Tab tab, float dx, float dy) {
        return new SelectedTabButton(parent, Registries.ITEM.get(tab.getIcon()).getDefaultStack(), dx, dy);
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        int x = (int) (baseX + 13);
        int y = (int) (baseY + 2);
        context.drawItem(icon, x, y);
    }

    @Override
    public boolean onClick(int button) {
        return false;
    }
}

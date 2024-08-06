package ru.feytox.etherology.gui.teldecore.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.data.Tab;
import ru.feytox.etherology.util.misc.Color;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

public class SelectedTabButton extends AbstractButton {

    private static final Identifier LEFT = EIdentifier.of("textures/gui/teldecore/icon/tab_left_2.png");
    private static final Identifier RIGHT = EIdentifier.of("textures/gui/teldecore/icon/tab_right_2.png");

    private final ItemStack icon;
    private final Color color;
    private final boolean isLeft;

    private SelectedTabButton(TeldecoreScreen parent, ItemStack icon, Color color, boolean isLeft, float dx, float dy) {
        super(parent, isLeft ? LEFT : RIGHT, null, dx, dy, 40, 25);
        this.icon = icon;
        this.color = color;
        this.isLeft = isLeft;
    }

    public static SelectedTabButton of(TeldecoreScreen parent, Tab tab, boolean isLeft, float dx, float dy) {
        return new SelectedTabButton(parent, Registries.ITEM.get(tab.getIcon()).getDefaultStack(), tab.getColor(), isLeft, dx, dy);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, texture);
        color.applyColor(context);
        RenderUtils.renderTexture(context, baseX, baseY, 0, 0, width, height, width, height);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        renderExtra(context, false);
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        context.push();
        context.translate(baseX + (isLeft ? 12 : 11), baseY + 2, 0);
        context.drawItem(icon, 0, 0);
        context.pop();
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        return false;
    }
}

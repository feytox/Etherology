package ru.feytox.etherology.gui.teldecore.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.data.Tab;
import ru.feytox.etherology.util.misc.Color;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

public class TabButton extends AbstractButton {

    private static final Identifier TAB = EIdentifier.of("textures/gui/teldecore/icon/tab_0.png");
    private static final Identifier HOVERED_TAB = EIdentifier.of("textures/gui/teldecore/icon/tab_1.png");

    private final Identifier target;
    private final ItemStack icon;
    private final Color color;

    private TabButton(TeldecoreScreen parent, Identifier target, ItemStack icon, Color color, float dx, float dy) {
        super(parent, TAB, HOVERED_TAB, dx, dy, 30, 25);
        this.target = target;
        this.icon = icon;
        this.color = color;
    }

    public static TabButton of(TeldecoreScreen parent, Identifier tabId, Tab tab, float dx, float dy) {
        return new TabButton(parent, tabId, Registries.ITEM.get(tab.getIcon()).getDefaultStack(), tab.getColor(), dx, dy);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = isMouseOver(mouseX, mouseY);
        Identifier texture = this.hoveredTexture == null || !hovered ? this.texture : this.hoveredTexture;
        RenderSystem.setShaderTexture(0, texture);
        color.applyColor(context);
        RenderUtils.renderTexture(context, baseX, baseY, 0, 0, width, height, width, height);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        renderExtra(context, hovered);
    }

    @Override
    protected void renderExtra(DrawContext context, boolean hovered) {
        context.push();
        context.translate(baseX + 13 - (hovered ? 1 : 0), baseY + 2, 0);
        context.drawItem(icon, 0, 0);
        context.pop();
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        return dataAction("Failed to handle click on tab %s button".formatted(target.toString()), data -> {
            data.switchTab(target);
            parent.clearAndInit();
            parent.executeOnPlayer(player -> player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 0.9f + 0.1f * player.getWorld().getRandom().nextFloat()));
        });
    }
}

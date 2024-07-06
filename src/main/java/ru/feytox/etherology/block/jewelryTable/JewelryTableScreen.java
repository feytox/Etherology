package ru.feytox.etherology.block.jewelryTable;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

public class JewelryTableScreen extends HandledScreen<JewelryTableScreenHandler> {

    public static final Identifier TEXTURE = EIdentifier.of("textures/gui/jewelry_table.png");

    public JewelryTableScreen(JewelryTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 203;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderButtons(context, x+40, y+11, mouseX, mouseY);
        renderBar(context, x+147, y+10);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    private void renderBar(DrawContext context, int x0, int y0) {
        ItemStack lens = handler.getTableInv().getStack(0);
        if (lens.isEmpty()) return;

        int height = MathHelper.ceil(98 * (1 - (float) lens.getDamage() / lens.getMaxDamage()));
        int y = y0 + 98 - height;
        int v = 110 - height;

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        RenderUtils.renderTexture(context, x0, y, 176, v, 8, height, 8, height, 256, 256);
    }
    
    private void renderButtons(DrawContext context, int x0, int y0, int mouseX, int mouseY) {
        if (handler.getTableInv().isEmpty()) return;

        for (int pos = 0; pos < 64; pos++) {
            if (JewelryTableInventory.EMPTY_CELLS.contains(pos)) continue;
            int x = x0 + (pos & 0b111) * 12;
            int y = y0 + ((pos >> 3) & 0b111) * 12;
            int offset = handler.getTableInv().getTextureOffset(pos);

            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            RenderUtils.renderTexture(context, x, y, 176 + 12 * offset, 0, 12, 12, 12, 12, 256, 256);
            if (mouseX <= x || mouseX >= x + 12 || mouseY <= y || mouseY >= y + 12) continue;

            RenderSystem.disableDepthTest();
            context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 12, y + 12, -2130706433, -2130706433, 0);
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        handleClick(40 + (this.width - this.backgroundWidth) / 2, 11 + (this.height - this.backgroundHeight) / 2, mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleClick(int x0, int y0, double mouseX, double mouseY, int button) {
        if (handler.getTableInv().isEmpty()) return;

        for (int pos = 0; pos < 64; pos++) {
            int x = x0 + (pos & 0b111) * 12;
            int y = y0 + ((pos >> 3) & 0b111) * 12;
            if (mouseX <= x || mouseX >= x + 12 || mouseY <= y || mouseY >= y + 12) continue;

            val manager = MinecraftClient.getInstance().interactionManager;
            if (manager == null) return;
            manager.clickButton(handler.syncId, pos + button * 100);
        }
    }
}

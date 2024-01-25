package ru.feytox.etherology.block.jewelryTable;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.RenderUtils;

public class JewelryTableScreen extends HandledScreen<JewelryTableScreenHandler> {

    public static final Identifier TEXTURE = new EIdentifier("textures/gui/jewelry_table.png");

    public JewelryTableScreen(JewelryTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 203;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderButtons(matrices, x+40, y+11, mouseX, mouseY);
        renderBar(matrices, x+147, y+10);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    private void renderBar(MatrixStack matrices, int x0, int y0) {
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

        RenderUtils.renderTexture(matrices, x0, y, 176, v, 8, height, 8, height, 256, 256);
    }
    
    private void renderButtons(MatrixStack matrices, int x0, int y0, int mouseX, int mouseY) {
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

            RenderUtils.renderTexture(matrices, x, y, 176 + 12 * offset, 0, 12, 12, 12, 12, 256, 256);
            if (mouseX <= x || mouseX >= x + 12 || mouseY <= y || mouseY >= y + 12) continue;

            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            fillGradient(matrices, x, y, x + 12, y + 12, -2130706433, -2130706433, getZOffset());
            RenderSystem.colorMask(true, true, true, true);
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

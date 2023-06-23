package ru.feytox.etherology.block.etherealFurnace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EtherealFurnaceScreen extends HandledScreen<EtherealFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new EIdentifier("textures/gui/ethereal_furnace.png");

    public EtherealFurnaceScreen(EtherealFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        float cookingPercent = 0;
        if (handler.isCooking() || handler.isDegrade()) {
            cookingPercent = handler.getCookingPercent();
            int i = MathHelper.floor(62 * cookingPercent);
            drawTexture(matrices, x+57, y+21, 176, 0, i, 12);
        }

        float fullFuelPercent = handler.getFuelPercent() - (0.7f * cookingPercent / EtherealFurnaceBlockEntity.MAX_FUEL);
        int j = MathHelper.floor(18 * fullFuelPercent);
        drawTexture(matrices, x+7, y+36, 176, 12, j, 4);
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
        titleY = 6;
        playerInventoryTitleY = backgroundHeight - 124;
    }
}

package ru.feytox.etherology.block.etherealFurnace;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EtherealFurnaceScreen extends HandledScreen<EtherealFurnaceScreenHandler> {
    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/ethereal_furnace.png");

    public EtherealFurnaceScreen(EtherealFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        float cookingPercent = 0;
        if (handler.isCooking() || handler.isDegrade()) {
            cookingPercent = handler.getCookingPercent();
            int i = MathHelper.floor(62 * cookingPercent);
            context.drawTexture(TEXTURE, x+57, y+21, 176, 0, i, 12);
        }

        float fullFuelPercent = handler.getFuelPercent() - (0.7f * cookingPercent / EtherealFurnaceBlockEntity.MAX_FUEL);
        int j = MathHelper.floor(18 * fullFuelPercent);
        context.drawTexture(TEXTURE, x+7, y+36, 176, 12, j, 4);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = 6;
        playerInventoryTitleY = backgroundHeight - 124;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, titleX, titleY, 0xFFE5E5E5, false);
        context.drawText(textRenderer, playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0xFFE5E5E5, false);
    }
}

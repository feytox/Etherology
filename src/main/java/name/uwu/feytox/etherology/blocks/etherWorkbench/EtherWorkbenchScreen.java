package name.uwu.feytox.etherology.blocks.etherWorkbench;

import com.mojang.blaze3d.systems.RenderSystem;
import name.uwu.feytox.etherology.util.EIdentifier;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EtherWorkbenchScreen extends HandledScreen<EtherWorkbenchScreenHandler> {
    private static final Identifier TEXTURE = new EIdentifier("textures/gui/ether_worckbench.png");

    public EtherWorkbenchScreen(EtherWorkbenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventoryTitleX = 116;
        this.playerInventoryTitleY = 88;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - 175) / 2;
        int y = (height - 195) / 2;
        drawTexture(matrices, x, y, 0, 0, 175, 195);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}

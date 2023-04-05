package ru.feytox.etherology.blocks.empowerTable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EmpowerTableScreen extends HandledScreen<EmpowerTableScreenHandler> {
    private static final Identifier TEXTURE = new EIdentifier("textures/gui/empowerment_table.png");
    private static final Identifier GLOW_RELA = new EIdentifier("textures/gui/empowerment_table/glow_rela.png");
    private static final Identifier GLOW_VIA = new EIdentifier("textures/gui/empowerment_table/glow_via.png");
    private static final Identifier GLOW_CLOS = new EIdentifier("textures/gui/empowerment_table/glow_clos.png");
    private static final Identifier GLOW_KETA = new EIdentifier("textures/gui/empowerment_table/glow_keta.png");
    private static final int AGE_GLOW = 40;
    private int glowTicks = 0;

    public EmpowerTableScreen(EmpowerTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - 175) / 2;
        int y = (height - 175) / 2;
        drawTexture(matrices, x, y, 0, 0, 175, 171);

        if (handler.shouldGlow()) {
            drawGlow(matrices, handler.getRela(), 0, x+23, y+17);
            drawGlow(matrices, handler.getVia(), 16, x+69, y+17);
            drawGlow(matrices, handler.getClos(), 32, x+23, y+63);
            drawGlow(matrices, handler.getKeta(), 48, x+69, y+63);
            if (glowTicks++ >= AGE_GLOW) glowTicks = 0;

        } else {
            glowTicks = 0;
        }
    }

    public void drawGlow(MatrixStack matrices, int value, int v, int x, int y) {
        if (value == 0) return;

        float alpha = glowTicks / (AGE_GLOW / 2f);
        alpha = alpha > 1 ? alpha - 1 : alpha;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        drawTexture(matrices, x, y, 176, v, 16, 16);
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
        titleY -= 7;
        playerInventoryTitleY += 6;
    }
}

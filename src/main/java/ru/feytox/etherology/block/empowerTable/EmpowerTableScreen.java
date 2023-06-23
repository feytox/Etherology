package ru.feytox.etherology.block.empowerTable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EmpowerTableScreen extends HandledScreen<EmpowerTableScreenHandler> {
    private static final Identifier TEXTURE = new EIdentifier("textures/gui/empowerment_table.png");
    private static final Identifier GLOW_RELA = new EIdentifier("textures/gui/glow_rela.png");
    private static final Identifier GLOW_VIA = new EIdentifier("textures/gui/glow_via.png");
    private static final Identifier GLOW_CLOS = new EIdentifier("textures/gui/glow_clos.png");
    private static final Identifier GLOW_KETA = new EIdentifier("textures/gui/glow_keta.png");
    private static final float AGE_GLOW = 60;
    private float glowAge = 0;

    public EmpowerTableScreen(EmpowerTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 176;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        matrices.push();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        matrices.pop();

        if (handler.shouldGlow()) {
            drawGlow(matrices, handler.getRela(), GLOW_RELA, x+23, y+17);
            drawGlow(matrices, handler.getVia(), GLOW_VIA, x+69, y+17);
            drawGlow(matrices, handler.getClos(), GLOW_CLOS, x+23, y+63);
            drawGlow(matrices, handler.getKeta(), GLOW_KETA, x+69, y+63);
            glowAge += delta;
            if (glowAge >= AGE_GLOW) glowAge = 0;

        } else {
            glowAge = 0;
        }
    }

    public void drawGlow(MatrixStack matrices, int value, Identifier texture, int x, int y) {
        if (value == 0) return;

        float alpha = glowAge / (AGE_GLOW / 2f);
        alpha = alpha > 1 ? Math.abs(alpha - 2) : alpha;

        matrices.push();
        RenderSystem.setShaderTexture(0, texture);
        drawTexturedQuad(matrices, x, y, alpha);
        matrices.pop();
    }

    private static void drawTexturedQuad(MatrixStack matrices, int x0, int y0, float alpha) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int x1 = x0 + 16;
        int y1 = y0 + 16;
        float u0 = 0;
        float u1 = 1.0f;
        float v0 = 0;
        float v1 = 1.0f;

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.enableBlend();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix, (float)x0, (float)y1, 0).texture(u0, v1).color(1.0F, 1.0F, 1.0F, alpha).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).texture(u1, v1).color(1.0F, 1.0F, 1.0F, alpha).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y0, 0).texture(u1, v0).color(1.0F, 1.0F, 1.0F, alpha).next();
        bufferBuilder.vertex(matrix, (float)x0, (float)y0, 0).texture(u0, v0).color(1.0F, 1.0F, 1.0F, alpha).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
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
        titleY = 5;
        playerInventoryTitleY = backgroundHeight - 94;
    }
}

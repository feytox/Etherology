package ru.feytox.etherology.client.block.empowerTable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ru.feytox.etherology.block.empowerTable.EmpowerTableScreenHandler;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EmpowerTableScreen extends HandledScreen<EmpowerTableScreenHandler> {
    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/empowerment_table.png");
    private static final Identifier GLOW_RELA = EIdentifier.of("textures/gui/glow_rela.png");
    private static final Identifier GLOW_VIA = EIdentifier.of("textures/gui/glow_via.png");
    private static final Identifier GLOW_CLOS = EIdentifier.of("textures/gui/glow_clos.png");
    private static final Identifier GLOW_KETA = EIdentifier.of("textures/gui/glow_keta.png");
    private static final float AGE_GLOW = 60;
    private float glowAge = 0;

    public EmpowerTableScreen(EmpowerTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 176;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.push();
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        context.pop();

        if (handler.shouldGlow()) {
            drawGlow(context, handler.getRela(), GLOW_RELA, x+23, y+17);
            drawGlow(context, handler.getVia(), GLOW_VIA, x+69, y+17);
            drawGlow(context, handler.getClos(), GLOW_CLOS, x+23, y+63);
            drawGlow(context, handler.getKeta(), GLOW_KETA, x+69, y+63);
            glowAge += delta;
            if (glowAge >= AGE_GLOW) glowAge = 0;

        } else {
            glowAge = 0;
        }
    }

    public void drawGlow(DrawContext context, int value, Identifier texture, int x, int y) {
        if (value == 0) return;

        float alpha = glowAge / (AGE_GLOW / 2f);
        alpha = alpha > 1 ? Math.abs(alpha - 2) : alpha;

        context.push();
        RenderSystem.setShaderTexture(0, texture);
        drawTexturedQuad(context, x, y, alpha);
        context.pop();
    }

    private static void drawTexturedQuad(DrawContext context, int x0, int y0, float alpha) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        int x1 = x0 + 16;
        int y1 = y0 + 16;
        float u0 = 0;
        float u1 = 1.0f;
        float v0 = 0;
        float v1 = 1.0f;

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.enableBlend();

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix, (float)x0, (float)y1, 0).texture(u0, v1).color(1.0F, 1.0F, 1.0F, alpha);
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).texture(u1, v1).color(1.0F, 1.0F, 1.0F, alpha);
        bufferBuilder.vertex(matrix, (float)x1, (float)y0, 0).texture(u1, v0).color(1.0F, 1.0F, 1.0F, alpha);
        bufferBuilder.vertex(matrix, (float)x0, (float)y0, 0).texture(u0, v0).color(1.0F, 1.0F, 1.0F, alpha);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
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
        titleY = 5;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, titleX, titleY, 0xFFE5E5E5, false);
        context.drawText(textRenderer, playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0xFFE5E5E5, false);
    }
}

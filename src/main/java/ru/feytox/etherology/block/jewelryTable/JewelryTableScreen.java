package ru.feytox.etherology.block.jewelryTable;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.RenderUtils;

import java.util.Map;
import java.util.function.Supplier;

public class JewelryTableScreen extends HandledScreen<JewelryTableScreenHandler> {

    public static final Identifier TEXTURE = new EIdentifier("textures/gui/jewelry_table.png");
    public static final Supplier<Map<Integer, Pair<Integer, Integer>>> buttonPositions = Suppliers.memoize(JewelryTableScreen::getButtonPositions);

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
        renderButtons(matrices, x+40, y+11);
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
    
    private void renderButtons(MatrixStack matrices, int x0, int y0) {
        if (handler.getTableInv().isEmpty()) return;
        val poses = buttonPositions.get();

        // TODO: 23.01.2024 simplfy to for-loop if it is faster
        poses.forEach((index, pos) -> {
            int offset = handler.getTableInv().getTextureOffset(index);

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            RenderUtils.renderTexture(matrices, x0 + pos.left(), y0 + pos.right(), 176 + 12 * offset, 0, 12, 12, 12, 12, 256, 256);
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        handleClick(40 + (this.width - this.backgroundWidth) / 2, 11 + (this.height - this.backgroundHeight) / 2, mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleClick(int x0, int y0, double mouseX, double mouseY, int button) {
        if (handler.getTableInv().isEmpty()) return;
        val poses = buttonPositions.get();

        poses.forEach((index, pos) -> {
            int x = x0 + pos.left();
            int y = y0 + pos.right();
            if (mouseX < x || mouseX > x + 12 || mouseY < y || mouseY > y + 12) return;

            val manager = MinecraftClient.getInstance().interactionManager;
            if (manager == null) return;
            manager.clickButton(handler.syncId, index + button * 100);
        });
    }

    private static Map<Integer, Pair<Integer, Integer>> getButtonPositions() {
        Map<Integer, Pair<Integer, Integer>> result = new Int2ObjectOpenHashMap<>();

        for (int index = 0; index < 52; index++) {
            int x, y;
            if (index < 4) {
                x = 24 + index * 12;
                y = 0;
            } else if (index < 10) {
                int i = index - 4;
                x = 12 + i * 12;
                y = 12;
            } else if (index < 42) {
                int i = index - 10;
                x = (i % 8) * 12;
                y = 24 + (i / 8) * 12;
            } else if (index < 48) {
                int i = index - 42;
                x = 12 + i * 12;
                y = 72;
            } else {
                int i = index - 48;
                x = 24 + i * 12;
                y = 84;
            }

            result.put(index, new IntIntImmutablePair(x, y));
        }

        return result;
    }
}

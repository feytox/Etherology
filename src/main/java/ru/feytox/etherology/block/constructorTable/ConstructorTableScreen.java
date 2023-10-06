package ru.feytox.etherology.block.constructorTable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import ru.feytox.etherology.block.pedestal.PedestalRenderer;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class ConstructorTableScreen extends HandledScreen<ConstructorTableScreenHandler> {
    private static final Identifier TEXTURE = new EIdentifier("textures/gui/constructor_table.png");
    private final PlayerInventory playerInventory;

    public ConstructorTableScreen(ConstructorTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventory = inventory;
        backgroundHeight = 197;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        drawItem(x + 116, y + 115, 145, handler.getSlot(0).getStack());
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
        titleY = 3;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    private void drawItem(int x, int y, int size, ItemStack stack) {
        // TODO: 06.10.2023 lags fix
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0F);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack renderStack = new MatrixStack();
        renderStack.translate(0.0F, 0.0F, 1000.0F);
        renderStack.scale(size, size, size);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(0 * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        renderStack.multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternionf2.conjugate();
        entityRenderDispatcher.setRotation(quaternionf2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        World world = playerInventory.player.getWorld();

        PedestalRenderer.renderVanillaGroundItem(renderStack, world, stack, immediate, 1.0F, 15728880, itemRenderer, Vec3d.ZERO, 0);
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }
}

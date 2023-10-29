package ru.feytox.etherology.block.inventorTable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import ru.feytox.etherology.block.pedestal.PedestalRenderer;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.List;

public class InventorTableScreen extends HandledScreen<InventorTableScreenHandler> {
    private static final Identifier TEXTURE = new EIdentifier("textures/gui/inventor_table.png");
    private final PlayerInventory playerInventory;
    private boolean aLotOfParts;
    private boolean scrollbarClicked;
    private float scrollPosition;
    private int visibleTopRow;

    public InventorTableScreen(InventorTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventory = inventory;
        backgroundHeight = 172;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        drawItem(x + 112, y + 78, 100, handler.getSlot(3).getStack());

        List<StaffPart> staffParts = handler.getStaffParts();
        aLotOfParts = staffParts.size() > 12;

        RenderSystem.setShaderTexture(0, TEXTURE);
        int k = (int) (27.0F * this.scrollPosition);
        this.drawTexture(matrices, x + 67, y + 33 + k, 232 + (aLotOfParts ? 0 : 12), 0, 12, 15);

        if (staffParts.isEmpty()) return;

        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 4; n++) {
                int row = m + visibleTopRow;
                int partIndex = row * 4 + n;
                if (partIndex >= staffParts.size()) return;
                StaffPart part = staffParts.get(partIndex);

                RenderSystem.setShaderTexture(0, TEXTURE);
                int partX = x + 8 + n * 14;
                int partY = y + 33 + m * 14;
                boolean highlighted = mouseX >= partX && mouseY >= partY && mouseX < partX + 14 && mouseY < partY + 14;

                int textureY = backgroundHeight;
                if (part.equals(handler.getSelectedPart())) {
                    textureY = backgroundHeight + 14;
                } else if (highlighted) {
                    textureY = backgroundHeight + 28;
                }

                this.drawTexture(matrices, partX, partY, 0, textureY, 14, 14);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrollbarClicked = false;
        if (client == null || client.interactionManager == null) return super.mouseClicked(mouseX, mouseY, button);

        int a = x + 8;
        int b = y + 33;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                double d = mouseX - (double)(a + j * 14);
                double e = mouseY - (double)(b + i * 14);
                int row = i + visibleTopRow;
                int partIndex = row * 4 + j;
                if (d >= 0.0 && e >= 0.0 && d < 14.0 && e < 14.0 && handler.onButtonClick(client.player, partIndex)) {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                    this.client.interactionManager.clickButton(handler.syncId, partIndex);
                    return true;
                }
            }
        }

        a = x + 67;
        if (aLotOfParts && mouseX >= a && mouseX < a + 12 && mouseY >= b && mouseY < b + 42) {
            this.scrollbarClicked = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = this.getRows() - 4;
        if (!scrollbarClicked || !aLotOfParts || i <= 0) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        int j = this.y + 13;
        int k = j + 56;
        scrollPosition = ((float)mouseY - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
        scrollPosition = MathHelper.clamp(scrollPosition, 0.0F, 1.0F);
        visibleTopRow = Math.max((int) (scrollPosition * i + 0.5), 0);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = this.getRows() - 4;
        if (!aLotOfParts || i <= 0) return true;

        float f = (float)amount / (float)i;
        scrollPosition = MathHelper.clamp(scrollPosition - f, 0.0F, 1.0F);
        visibleTopRow = Math.max((int) (scrollPosition * i + 0.5F), 0);

        return true;
    }

    private int getRows() {
        return MathHelper.ceilDiv(handler.getStaffParts().size(), 4);
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
        // TODO: 06.10.2023 fix animation lag
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

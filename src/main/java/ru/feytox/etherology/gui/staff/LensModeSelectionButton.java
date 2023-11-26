package ru.feytox.etherology.gui.staff;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import lombok.AllArgsConstructor;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Math;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.RenderUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

@AllArgsConstructor
public class LensModeSelectionButton extends BaseComponent {

    private static final Identifier BUTTON_TEXTURE = new EIdentifier("textures/gui/staff_mode_selection.png");
    private final int u;
    @Setter
    private boolean active;


    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        RenderSystem.setShaderTexture(0, BUTTON_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.push();

        float scaleX = width / 29.0f;
        float scaleY = height / 47.0f;

        matrices.scale(scaleX, scaleY, 0);

        float x = this.x + width * (1 - scaleX) / 2;
        float y = this.y + height * (1 - scaleY) / 2;

        drawButtonTexture(matrices, x, scaleX, y, scaleY, 0);

        if (active) drawButtonTexture(matrices, x, scaleX, y, scaleY, 50);

        RenderSystem.disableBlend();
        matrices.pop();
    }

    private void drawButtonTexture(MatrixStack matrices, float x, float scaleX, float y, float scaleY, int v0) {
        RenderUtils.renderTexture(matrices, x / scaleX, y / scaleY, u + 11, v0 + 2, width, height, 27, 49, 128, 128);
    }

    public static LensModeSelectionButton create(boolean isUp, boolean isActive, FlowLayout parent, Consumer<LensSelectionType> selectedConsumer, Supplier<LensSelectionType> selectedSupplier, int backSize) {
        LensModeSelectionButton button = new LensModeSelectionButton(isUp ? 0 : 50, isActive);
        button.mouseEnter().subscribe(() -> {
            selectedConsumer.accept(isUp ? LensSelectionType.UP_ARROW : LensSelectionType.DOWN_ARROW);
        });
        button.mouseLeave().subscribe(() -> {
            if (selectedSupplier.get().equals(isUp ? LensSelectionType.UP_ARROW : LensSelectionType.DOWN_ARROW)) {
                    selectedConsumer.accept(LensSelectionType.NONE);
            }
        });

        float xScale = 1.0f * backSize / 512.0f;
        float yScale = 1.0f * backSize / 512.0f;
        button.sizing(Sizing.fixed(Math.round(27 * xScale)), Sizing.fixed(Math.round(49 * yScale)));

        parent.child(button.positioning(Positioning.relative(isUp ? 40 : 60, 50)));
        return button;
    }
}

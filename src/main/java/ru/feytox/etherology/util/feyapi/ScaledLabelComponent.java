package ru.feytox.etherology.util.feyapi;

import io.wispforest.owo.ui.component.LabelComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ScaledLabelComponent extends LabelComponent {
    private final float scale;

    public ScaledLabelComponent(Text text, float scale) {
        super(text);
        this.scale = scale;
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        matrices.push();
        matrices.scale(scale, scale, scale);
        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
        matrices.pop();
    }
}

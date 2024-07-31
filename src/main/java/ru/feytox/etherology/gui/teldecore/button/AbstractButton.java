package ru.feytox.etherology.gui.teldecore.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.util.misc.RenderUtils;

public abstract class AbstractButton extends ParentedWidget {

    private final int width;
    private final int height;
    private final Identifier texture;
    @Nullable
    private final Identifier hoveredTexture;
    protected boolean active = true;

    public AbstractButton(TeldecoreScreen parent, Identifier texture, @Nullable Identifier hoveredTexture, float dx, float dy, int width, int height) {
        super(parent, parent.getX()+dx, parent.getY()+dy);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
    }

    public abstract boolean onClick(int button);

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && isMouseOver(mouseX, mouseY)) return onClick(button);
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier texture = hoveredTexture == null || !isMouseOver(mouseX, mouseY) ? this.texture : hoveredTexture;
        RenderSystem.setShaderTexture(0, texture);
        RenderUtils.renderTexture(context, baseX, baseY, 0, 0, width, height, width, height);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && mouseX > baseX && mouseY > baseY && mouseX < baseX+width && mouseY < baseY+height;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.HOVERED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}
}

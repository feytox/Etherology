package ru.feytox.etherology.gui.teldecore.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.data.TeldecoreComponent;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.function.Consumer;

public abstract class AbstractButton extends ParentedWidget {

    protected final int width;
    protected final int height;
    protected final Identifier texture;
    @Nullable
    protected final Identifier hoveredTexture;
    protected boolean active = true;

    public AbstractButton(TeldecoreScreen parent, Identifier texture, @Nullable Identifier hoveredTexture, float baseX, float baseY, float dx, float dy, int width, int height) {
        super(parent, baseX+dx, baseY+dy);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
    }

    public AbstractButton(TeldecoreScreen parent, Identifier texture, @Nullable Identifier hoveredTexture, float dx, float dy, int width, int height) {
        this(parent, texture, hoveredTexture, parent.getX(), parent.getY(), dx, dy, width, height);
    }

    public abstract boolean onClick(double mouseX, double mouseY, int button);

    protected boolean dataAction(String errorMsg, Consumer<TeldecoreComponent> dataConsumer) {
        TeldecoreComponent data = parent.getData().orElse(null);
        if (data == null) {
            Etherology.ELOGGER.error(errorMsg);
            return false;
        }
        dataConsumer.accept(data);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && isMouseOver(mouseX, mouseY)) return onClick(mouseX, mouseY, button);
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = isMouseOver(mouseX, mouseY);
        Identifier texture = hoveredTexture == null || !hovered ? this.texture : hoveredTexture;
        RenderSystem.setShaderTexture(0, texture);
        RenderUtils.renderTexture(context, baseX, baseY, 0, 0, width, height, width, height);
        renderExtra(context, hovered);
    }

    protected void renderExtra(DrawContext context, boolean hovered) {}

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

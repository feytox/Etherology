package ru.feytox.etherology.gui.teldecore;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;

public class TeldecoreScreen extends Screen {

    // static
    public static final Identifier BASE;
    private static final int BASE_WIDTH = 310;
    private static final int BASE_HEIGHT = 210;

    // screen data
    private final Screen parent;
    @Getter
    private float x;
    @Getter
    private float y;

    public TeldecoreScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        x = (width - BASE_WIDTH) / 2.0f;
        y = (height - BASE_HEIGHT) / 2.0f;

        List<AbstractPage> pages = Chapter.TEST.toPages(this);
        Etherology.ELOGGER.info("{}", pages.size());
        pages.forEach(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // TODO: 30.07.2024 todo
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        RenderSystem.setShaderTexture(0, BASE);
        RenderUtils.renderTexture(context, x, y, 0, 0, BASE_WIDTH, BASE_HEIGHT, BASE_WIDTH, BASE_HEIGHT);
    }

    @Override
    public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        return super.addDrawableChild(drawableElement);
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    static {
        BASE = EIdentifier.of("textures/gui/teldecore/page/base.png");
    }
}

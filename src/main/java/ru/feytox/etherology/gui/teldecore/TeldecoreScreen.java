package ru.feytox.etherology.gui.teldecore;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.button.ChapterButton;
import ru.feytox.etherology.gui.teldecore.button.TurnPageButton;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.data.TeldecoreComponent;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.registry.misc.RegistriesRegistry;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TeldecoreScreen extends Screen {

    // static
    public static final Identifier CHAPTER_MENU;
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
    public void init() {
        super.init();
        x = (width - BASE_WIDTH) / 2.0f;
        y = (height - BASE_HEIGHT) / 2.0f;

        getData().ifPresentOrElse(this::initPages, () -> Etherology.ELOGGER.error("Could not get player's teldecore data."));
    }

    @Override @Deprecated
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_H) {
            getData().ifPresent(data -> {
                data.setSelected(CHAPTER_MENU);
                clearAndInit();
            });
            return true;
        }

        for (Element child : children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void initPages(TeldecoreComponent data) {
        Identifier selected = data.getSelected();
        // TODO: 31.07.2024 use smth better
        if (selected.equals(CHAPTER_MENU)) {
            initChapterMenu();
            return;
        }
        if (initChapter(data, selected)) return;
        Etherology.ELOGGER.error("Failed to get chapter {}, falling back to default", selected);
        data.setSelected(CHAPTER_MENU);
        initChapterMenu();
    }

    private void initChapterMenu() {
        Registry<Chapter> chapterRegistry = getChapters();
        if (chapterRegistry == null) return;

        Iterator<RegistryEntry.Reference<Chapter>> chapters = chapterRegistry.streamEntries().iterator();

        int i = 0;
        while (chapters.hasNext()) {
            RegistryEntry.Reference<Chapter> reference = chapters.next();
            Identifier target = reference.registryKey().getValue();
            ItemStack icon = Registries.ITEM.get(reference.value().getIcon()).getDefaultStack();
            addDrawableChild(new ChapterButton(this, target, icon, 15+i*32, 15));
            i++;
        }
    }

    private boolean initChapter(TeldecoreComponent data, Identifier selected) {
        Registry<Chapter> chapters = getChapters();
        if (chapters == null) return false;

        Chapter chapter = chapters.get(selected);
        if (chapter == null) return false;

        List<AbstractPage> pages = chapter.toPages(this);
        int page = 2 * data.getPage();
        if (pages.size() < page) {
            page = 0;
            data.setPage(0);
        }
        addPage(pages.get(page), page, pages.size());
        if (page+1 < pages.size()) addPage(pages.get(page+1), page+1, pages.size());
        return true;
    }

    @Nullable
    private Registry<Chapter> getChapters() {
        if (client == null || client.world == null) return null;
        return client.world.getRegistryManager().get(RegistriesRegistry.CHAPTERS);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        RenderSystem.setShaderTexture(0, BASE);
        RenderUtils.renderTexture(context, x, y, 0, 0, BASE_WIDTH, BASE_HEIGHT, BASE_WIDTH, BASE_HEIGHT);
    }

    @Override // for public access
    public void clearAndInit() {
        super.clearAndInit();
    }

    private void addPage(AbstractPage page, int pageId, int pages) {
        addDrawableChild(page);
        page.initContent();
        if (page.isLeft() && pageId > 0) addDrawableChild(TurnPageButton.of(this, true));
        if (!page.isLeft() && pageId+1 < pages) addDrawableChild(TurnPageButton.of(this, false));
    }

    @Override // for public access
    public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        return super.addDrawableChild(drawableElement);
    }

    public Optional<TeldecoreComponent> getData() {
        if (client == null || client.player == null) return Optional.empty();
        return EtherologyComponents.TELDECORE.maybeGet(client.player);
    }

    public void executeOnPlayer(Consumer<PlayerEntity> playerConsumer) {
        if (client != null && client.player != null) playerConsumer.accept(client.player);
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
        CHAPTER_MENU = EIdentifier.of("chapter_menu");
    }
}

package ru.feytox.etherology.gui.teldecore;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.button.AbstractButton;
import ru.feytox.etherology.gui.teldecore.button.SelectedTabButton;
import ru.feytox.etherology.gui.teldecore.button.TabButton;
import ru.feytox.etherology.gui.teldecore.button.TurnPageButton;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.data.Tab;
import ru.feytox.etherology.gui.teldecore.data.TeldecoreComponent;
import ru.feytox.etherology.gui.teldecore.misc.FeyIngredient;
import ru.feytox.etherology.gui.teldecore.misc.FocusedIngredientProvider;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.registry.misc.RegistriesRegistry;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.*;
import java.util.function.Consumer;

public class TeldecoreScreen extends Screen implements FocusedIngredientProvider {

    // static
    public static final Identifier CHAPTER_MENU;
    public static final Identifier BASE;
    public static final int BASE_WIDTH = 310;
    public static final int BASE_HEIGHT = 210;

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

    private void initPages(TeldecoreComponent data) {
        Identifier selected = data.getSelected();
        // TODO: 31.07.2024 use smth better
        if (selected.equals(CHAPTER_MENU)) {
            initChapterMenu(data);
            return;
        }
        if (initChapter(data, selected)) return;
        Etherology.ELOGGER.error("Failed to get chapter {}, falling back to default", selected);
        data.setSelectedChapter(CHAPTER_MENU);
        initChapterMenu(data);
    }

    private void initChapterMenu(TeldecoreComponent data) {
        Registry<Tab> tabs = getRegistry(RegistriesRegistry.TABS);
        if (tabs == null) return;

        Optional<RegistryEntry.Reference<Tab>> optionalTab = data.getTab() != null ? tabs.getEntry(data.getTab()) : Optional.empty();
        RegistryEntry.Reference<Tab> tabEntry = optionalTab.or(tabs::getDefaultEntry).orElseThrow(() -> new NoSuchElementException("Could not find any teldecore tab."));
        initSelectedTab(tabEntry.value());
        initTabs(tabs, tabEntry.registryKey());
    }

    private void initSelectedTab(Tab tab) {
        tab.addPages(this);
    }

    private void initTabs(@Nullable Registry<Tab> tabsRegistry, @Nullable RegistryKey<Tab> selected) {
        tabsRegistry = tabsRegistry == null ? getRegistry(RegistriesRegistry.TABS) : tabsRegistry;
        if (tabsRegistry == null) return;

        val tabs = tabsRegistry.streamEntries().sorted(Comparator.comparingInt(ref -> ref.value().getTabId())).iterator();

        int i = 0;
        while (tabs.hasNext()) {
            RegistryEntry.Reference<Tab> tabEntry = tabs.next();
            AbstractButton button = selected != null && tabEntry.matchesKey(selected) ? SelectedTabButton.of(this, tabEntry.value(), -18, 12+i*29)
                    : TabButton.of(this, tabEntry.registryKey().getValue(), tabEntry.value(), -18, 12+i*29);
            addDrawableChild(button);
            i++;
        }
    }

    private boolean initChapter(TeldecoreComponent data, Identifier selected) {
        Registry<Chapter> chapters = getRegistry(RegistriesRegistry.CHAPTERS);
        if (chapters == null) return false;

        Chapter chapter = chapters.get(selected);
        if (chapter == null) return false;

        List<AbstractPage> pages = chapter.toPages(this);
        int page = 2 * data.getPage();
        if (pages.size() < page) {
            page = 0;
            data.setChapterPage(0);
        }
        addPage(pages.get(page), page, pages.size());
        if (page+1 < pages.size()) addPage(pages.get(page+1), page+1, pages.size());
        initTabs(null, null);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child : children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (Element child : children()) {
            if (child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        }
        return false;
    }

    @Nullable
    public <T> Registry<T> getRegistry(RegistryKey<? extends Registry<? extends T>> registryKey) {
        if (client == null || client.world == null) return null;
        return client.world.getRegistryManager().get(registryKey);
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

    @Override @Nullable
    public FeyIngredient getFocusedIngredient(int mouseX, int mouseY) {
        return children().stream().map(child -> {
            if (!(child instanceof FocusedIngredientProvider provider)) return null;
            return provider.getFocusedIngredient(mouseX, mouseY);
        }).filter(Objects::nonNull).findAny().orElse(null);
    }
}

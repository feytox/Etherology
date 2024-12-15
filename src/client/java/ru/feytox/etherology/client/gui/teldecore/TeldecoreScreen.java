package ru.feytox.etherology.client.gui.teldecore;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.client.gui.teldecore.button.SelectedTabButton;
import ru.feytox.etherology.client.gui.teldecore.button.TabButton;
import ru.feytox.etherology.client.gui.teldecore.button.TurnPageButton;
import ru.feytox.etherology.client.gui.teldecore.misc.FeyIngredient;
import ru.feytox.etherology.client.gui.teldecore.misc.FocusedIngredientProvider;
import ru.feytox.etherology.client.gui.teldecore.page.*;
import ru.feytox.etherology.client.util.RenderUtils;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.gui.teldecore.data.Tab;
import ru.feytox.etherology.gui.teldecore.data.TeldecoreComponent;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.registry.misc.RegistriesRegistry;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class TeldecoreScreen extends Screen implements FocusedIngredientProvider {

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

        getData().ifPresentOrElse(this::initPages, () -> Etherology.ELOGGER.error("Could not get player's teldecore teldecoreData."));
    }

    private void initPages(TeldecoreComponent data) {
        var selected = data.getSelected();
        // TODO: 31.07.2024 use smth better
        if (selected.equals(TeldecoreComponent.CHAPTER_MENU)) {
            initChapterMenu(data);
            return;
        }
        if (initChapter(data, selected)) return;
        Etherology.ELOGGER.error("Failed to get chapter {}, falling back to default", selected);
        data.setSelectedChapter(TeldecoreComponent.CHAPTER_MENU);
        initChapterMenu(data);
    }

    private void initChapterMenu(TeldecoreComponent data) {
        var tabs = getRegistry(RegistriesRegistry.TABS);
        if (tabs == null) return;

        Optional<RegistryEntry.Reference<Tab>> optionalTab = data.getTab() != null ? tabs.getEntry(data.getTab()) : Optional.empty();
        var tabEntry = optionalTab.or(tabs::getDefaultEntry).orElseThrow(() -> new NoSuchElementException("Could not find any teldecore tab."));
        initSelectedTab(tabEntry.value());
        initTabs(tabs, tabEntry.registryKey());
    }

    private void initSelectedTab(Tab tab) {
        addPages(tab);
    }

    public void addPages(Tab tab) {
        var chapterRegistry = getRegistry(RegistriesRegistry.CHAPTERS);
        if (chapterRegistry == null) {
            Etherology.ELOGGER.error("Failed to load chapters registry.");
            return;
        }

        var text = Text.translatable(tab.titleKey());
        var left = new TitlePage(this, text, true, true);
        tab.contents().forEach(content -> {
            if (!left.addContent(content, 10)) Etherology.ELOGGER.error("Failed to fit all contents on tab \"{}\" ", text.getString());
        });

        Function<Identifier, Chapter> idToChapter = id -> {
            var chapter = chapterRegistry.get(id);
            if (chapter != null) return chapter;
            Etherology.ELOGGER.error("Failed to load chapter \"{}\". Closing screen to prevent errors.", text.getString());
            close();
            return null;
        };
        var teldecoreData = maybeGetTeldecoreData().orElseThrow(() -> new NoSuchElementException("Failed to get teldecore teldecoreData for client player"));

        addDrawableChild(new ResearchTreePage(this, teldecoreData, tab.tree(), idToChapter, false));
        addDrawableChild(left);
        left.initContent();
    }

    private void initTabs(@Nullable Registry<Tab> tabsRegistry, @Nullable RegistryKey<Tab> selected) {
        tabsRegistry = tabsRegistry == null ? getRegistry(RegistriesRegistry.TABS) : tabsRegistry;
        if (tabsRegistry == null) return;

        val tabs = tabsRegistry.streamEntries().filter(ref -> ref.value().show())
                .sorted(Comparator.comparingInt(ref -> ref.value().tabId())).iterator();

        var i = 0;
        while (tabs.hasNext()) {
            var tabEntry = tabs.next();
            var isLeft = i < 6;
            var button = selected != null && tabEntry.matchesKey(selected) ? SelectedTabButton.of(this, tabEntry.value(), isLeft, isLeft ? -18 : 288, 12+i*29)
                    : TabButton.of(this, tabEntry.registryKey().getValue(), tabEntry.value(), isLeft, isLeft ? -18 : 298, 12+i*29);
            addDrawableChild(button);
            i++;
        }
    }

    private boolean initChapter(TeldecoreComponent data, Identifier selected) {
        var chapters = getRegistry(RegistriesRegistry.CHAPTERS);
        if (chapters == null) return false;

        var chapter = chapters.get(selected);
        if (chapter == null) return false;

        var pages = toPages(chapter, this, data, selected);
        var page = 2 * data.getPage();
        if (pages.size() < page) {
            page = 0;
            data.setPage(0);
        }
        addPage(pages.get(page), page, pages.size());
        if (page+1 < pages.size()) addPage(pages.get(page+1), page+1, pages.size());
        initTabs(null, null);
        return true;
    }

    public static List<AbstractPage> toPages(Chapter chapter, TeldecoreScreen screen, TeldecoreComponent data, Identifier chapterId) {
        var pages = new ObjectArrayList<AbstractPage>();
        var title = Text.translatable(chapter.titleKey());

        chapter.quest().ifPresent(quest -> {
            if (data.isCompleted(chapterId)) return;
            var page = new QuestPage(screen, quest, chapterId, true);
            for (var content : quest.contents()) {
                if (!page.addContent(content, 10)) Etherology.ELOGGER.error("Found a content in the chapter \"{}\", that doesn't fit in quest info.", title);
            }
            pages.add(page);
        });

        var hasQuest = !pages.isEmpty();
        pages.add(new TitlePage(screen, title, !hasQuest, true));

        for (var content : chapter.contents()) {
            if (!content.getBehaviour().test(hasQuest)) continue;

            var lastPage = pages.getLast();
            if (lastPage.addContent(content, 10)) continue;

            var page = new EmptyPage(screen, !lastPage.isLeft());
            if (!page.addContent(content, 10)) {
                Etherology.ELOGGER.error("Found a content in the chapter \"{}\", that doesn't fit anywhere.", title);
                return pages;
            }
            pages.add(page);
        }

        return pages;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (var child : children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (var child : children()) {
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
        page.setPageIndex(pageId+1);
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

    @Override
    public boolean shouldPause() {
        return false;
    }

    static {
        BASE = EIdentifier.of("textures/gui/teldecore/page/base.png");
    }

    @Override @Nullable
    public FeyIngredient getFocusedIngredient(int mouseX, int mouseY) {
        return children().stream().map(child -> {
            if (!(child instanceof FocusedIngredientProvider provider)) return null;
            return provider.getFocusedIngredient(mouseX, mouseY);
        }).filter(Objects::nonNull).findAny().orElse(null);
    }

    public static void renderText(DrawContext context, TextRenderer textRenderer, Text text, float x, float y) {
        textRenderer.draw(text, x, y, 0x70523D, false, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }

    public static void renderText(DrawContext context, TextRenderer textRenderer, OrderedText text, float x, float y) {
        textRenderer.draw(text, x, y, 0x70523D, false, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }

    public static Optional<TeldecoreComponent> maybeGetTeldecoreData() {
        return Optional.ofNullable(MinecraftClient.getInstance().player).flatMap(EtherologyComponents.TELDECORE::maybeGet);
    }
}

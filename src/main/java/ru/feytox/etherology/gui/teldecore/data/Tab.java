package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.page.ResearchTreePage;
import ru.feytox.etherology.gui.teldecore.page.TitlePage;
import ru.feytox.etherology.registry.misc.RegistriesRegistry;
import ru.feytox.etherology.util.misc.Color;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class Tab {

    public static final Codec<Tab> CODEC;

    @Getter
    private final int tabId;
    @Getter
    private final Identifier icon;
    private final String titleKey;
    @Getter
    private final Color color;
    private final List<AbstractContent> contents;
    private final ResearchTree tree;

    @Environment(EnvType.CLIENT)
    public void addPages(TeldecoreScreen screen) {
        Registry<Chapter> chapterRegistry = screen.getRegistry(RegistriesRegistry.CHAPTERS);
        if (chapterRegistry == null) {
            Etherology.ELOGGER.error("Failed to load chapters registry.");
            return;
        }

        Text text = Text.translatable(titleKey);
        TitlePage left = new TitlePage(screen, text, true, true);
        contents.forEach(content -> {
            if (!left.addContent(content, 10)) Etherology.ELOGGER.error("Failed to fit all contents on tab \"{}\" ", text.getString());
        });

        Function<Identifier, Chapter> idToIcon = id -> {
            Chapter chapter = chapterRegistry.get(id);
            if (chapter != null) return chapter;
            Etherology.ELOGGER.error("Failed to load chapter \"{}\". Closing screen to prevent errors.", text.getString());
            screen.close();
            return null;
        };

        screen.addDrawableChild(new ResearchTreePage(screen, tree, idToIcon, false));
        screen.addDrawableChild(left);
        left.initContent();
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("tab_id").forGetter(t -> t.tabId),
                Identifier.CODEC.fieldOf("icon").forGetter(t -> t.icon),
                Codec.STRING.fieldOf("title").forGetter(t -> t.titleKey),
                Color.CODEC.fieldOf("color").forGetter(t -> t.color),
                Chapter.CONTENT_CODEC.listOf().fieldOf("content").forGetter(t -> t.contents),
                ResearchTree.CODEC.fieldOf("chapters").forGetter(t -> t.tree)
        ).apply(instance, Tab::new));
    }
}

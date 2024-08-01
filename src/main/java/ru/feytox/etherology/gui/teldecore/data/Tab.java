package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.page.EmptyPage;
import ru.feytox.etherology.gui.teldecore.page.TitlePage;

import java.util.List;

@RequiredArgsConstructor
public class Tab {

    public static final Codec<Tab> CODEC;

    @Getter
    private final int tabId;
    @Getter
    private final Identifier icon;
    private final String titleKey;
    private final List<AbstractContent> contents;
    private final ChapterGrid grid;

    @Environment(EnvType.CLIENT)
    public void addPages(TeldecoreScreen screen) {
        Text text = Text.translatable(titleKey);
        TitlePage left = new TitlePage(screen, text, true, true);
        contents.forEach(content -> {
            if (!left.addContent(content, 6)) Etherology.ELOGGER.error("Failed to fit all contents on tab \"{}\" ", text.getString());
        });

        EmptyPage right = new EmptyPage(screen, false);
        screen.addDrawableChild(left);
        screen.addDrawableChild(right);
        left.initContent();
        right.initContent();
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("tab_id").forGetter(t -> t.tabId),
                Identifier.CODEC.fieldOf("icon").forGetter(t -> t.icon),
                Codec.STRING.fieldOf("title").forGetter(t -> t.titleKey),
                Chapter.CONTENT_CODEC.listOf().fieldOf("content").forGetter(t -> t.contents),
                ChapterGrid.CODEC.fieldOf("chapters").forGetter(t -> t.grid)
        ).apply(instance, Tab::new));
    }
}

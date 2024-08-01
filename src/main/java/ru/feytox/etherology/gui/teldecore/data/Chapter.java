package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.content.ImageContent;
import ru.feytox.etherology.gui.teldecore.content.TextContent;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.gui.teldecore.page.EmptyPage;
import ru.feytox.etherology.gui.teldecore.page.TitlePage;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class Chapter {

    private static final Map<String, MapCodec<? extends AbstractContent>> CONTENT_TYPES;
    public static final Codec<AbstractContent> CONTENT_CODEC;
    public static final Codec<Chapter> CODEC;

    @Getter
    private final Identifier icon;
    private final String titleKey;
    private final List<AbstractContent> contents;

    @Environment(EnvType.CLIENT)
    public List<AbstractPage> toPages(TeldecoreScreen screen) {
        List<AbstractPage> pages = new ObjectArrayList<>();
        Text title = Text.translatable(titleKey);
        pages.add(new TitlePage(screen, title, true, true));

        for (AbstractContent content : contents) {
            AbstractPage lastPage = pages.getLast();
            if (lastPage.addContent(content, 6)) continue;
            EmptyPage page = new EmptyPage(screen, !lastPage.isLeft());
            if (!page.addContent(content, 6)) {
                Etherology.ELOGGER.error("Found a content in the chapter \"{}\", that doesn't fit anywhere.", title);
                return pages;
            }
            pages.add(page);
        }

        return pages;
    }

    static {
        CONTENT_TYPES = Map.of(
                "text", TextContent.CODEC, "image", ImageContent.CODEC
        );
        CONTENT_CODEC = Codec.STRING.dispatch(AbstractContent::getType, CONTENT_TYPES::get);

        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("icon").forGetter(c -> c.icon),
                Codec.STRING.fieldOf("title").forGetter(c -> c.titleKey),
                CONTENT_CODEC.listOf().fieldOf("content").forGetter(c -> c.contents)
        ).apply(instance, Chapter::new));
    }
}

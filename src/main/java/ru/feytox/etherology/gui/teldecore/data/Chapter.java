package ru.feytox.etherology.gui.teldecore.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
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
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

@RequiredArgsConstructor
public class Chapter {

    public static final Chapter TEST;

    private final Identifier id;
    private final Text title;
    private final List<AbstractContent> contents;

    public List<AbstractPage> toPages(TeldecoreScreen screen) {
        List<AbstractPage> pages = new ObjectArrayList<>();
        pages.add(new TitlePage(screen, title, true, true));

        for (AbstractContent content : contents) {
            AbstractPage lastPage = pages.getLast();
            if (lastPage.addContent(content, 6)) continue;
            EmptyPage page = new EmptyPage(screen, !lastPage.isLeft());
            if (!page.addContent(content, 6)) {
                Etherology.ELOGGER.error("Found a content in the chapter {}, that doesn't fit anywhere.", id);
                return pages;
            }
            pages.add(page);
        }

        return pages;
    }

    // TODO: 30.07.2024 remove test
    static {
        TEST = new Chapter(EIdentifier.of("test"), Text.of("Стол Великоестествия"), List.of(
                TextContent.of(Text.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam porttitor eros et lobortis euismod. In sit amet vehicula nunc, eget elementum diam. Morbi tincidunt enim in massa facilisis, quis ultricies magna consequat.")),
                TextContent.of(Text.of("Lorem ipsum dolor sit amet...")),
                TextContent.of(Text.of("Fusce nulla arcu, blandit ac consequat et, finibus non felis. Fusce ultrices auctor eros, et fermentum leo vestibulum vel. ")),
                TextContent.of(Text.of("Praesent euismod malesuada nulla, quis posuere odio pharetra in. Vivamus vel nibh elementum, semper elit nec, eleifend quam. Morbi tempus ac ligula at eleifend. Curabitur odio nunc, vestibulum in nulla sit amet, faucibus pulvinar augue.")),
                TextContent.of(Text.of("Praesent euismod malesuada nulla, quis posuere odio pharetra in. Vivamus vel nibh elementum, semper elit nec, eleifend quam.")),
                TextContent.of(Text.of("Morbi tempus ac ligula at eleifend. Curabitur odio nunc, vestibulum in nulla sit amet, faucibus pulvinar augue.")),
                ImageContent.of(EIdentifier.of("textures/gui/teldecore/image/test1.png"), 1920, 1080)
        ));
    }
}

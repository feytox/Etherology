package ru.feytox.etherology.gui.teldecore.chapters;

import ru.feytox.etherology.gui.teldecore.Chapter;
import ru.feytox.etherology.gui.teldecore.pages.EmptyPage;
import ru.feytox.etherology.gui.teldecore.pages.TextPage;

import java.util.ArrayList;
import java.util.List;

public class ExampleChapter extends Chapter {
    public ExampleChapter() {
        super(1, getChapterPages());
    }

    private static List<EmptyPage> getChapterPages() {
        List<TextPage> pages = TextPage.getTextPages("Example",
                "aboba bebra foobar bazqux cuuux aboba bebra foobar bazqux cuuux aboba bebra foobar bazqux cuuux" +
                        " dsakldaskdashdkasjdhaskdhaslkdsahl \rd;ksaldjas;djasd;lasdajsh;fas;fashjf;asfas;lfhsa;fhasf;" +
                        ";dasjd;asldjsa;ldjas;dasjd;las\rdjasldjsaldjasldjasldasjdlasjdasldjasldjasldasjdlsaj");
        return new ArrayList<>(pages);
    }
}

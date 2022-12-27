package name.uwu.feytox.lotyh.gui.teldecore.chapters;

import name.uwu.feytox.lotyh.gui.teldecore.Chapter;
import name.uwu.feytox.lotyh.gui.teldecore.pages.EmptyPage;
import name.uwu.feytox.lotyh.gui.teldecore.pages.TextPage;

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

package ru.feytox.etherology.gui.teldecore;

import net.minecraft.client.MinecraftClient;
import ru.feytox.etherology.gui.teldecore.pages.EmptyPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chapter {
    int chapterId;
    List<EmptyPage> chapterPages;
    int currentPageNum = 0;

    public Chapter(int chapterId, List<EmptyPage> pages) {
        this.chapterPages = pages;
        if (this.chapterPages.size() % 2 == 1) {
            this.chapterPages.add(new EmptyPage(false));
        }
        this.chapterId = chapterId;
    }

    public Chapter(int chapterId, EmptyPage... pages) {
        this(chapterId, Arrays.stream(pages).toList());
    }

    public void open() {
        this.refreshButtons();
        List<EmptyPage> pagesToOpen = new ArrayList<>(Arrays.asList(
                this.chapterPages.get(this.currentPageNum), this.chapterPages.get(this.currentPageNum+1)));
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new TeldecoreScreen(pagesToOpen));
    }

    public void refreshButtons() {
        this.chapterPages.forEach(EmptyPage::refreshButtons);
    }

    public void nextTwo() {
        if (this.currentPageNum + 2 < this.chapterPages.size()) {
            this.currentPageNum += 2;
            this.open();
        }
    }

    public void prevTwo() {
        if (this.currentPageNum != 0) {
            this.currentPageNum -= 2;
            this.open();
        }
    }

    public boolean isFirst() {
        return currentPageNum == 0;
    }

    public boolean isLast() {
        return this.currentPageNum + 2 >= this.chapterPages.size();
    }
}

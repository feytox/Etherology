package name.uwu.feytox.lotyh.gui.teldecore;

import name.uwu.feytox.lotyh.gui.teldecore.pages.MainPage;

import java.util.*;

public class Chapters {
    Map<Integer, Chapter> chapterMap;
    int currentChapterId = 0;


    public Chapters() {
        this.chapterMap = new HashMap<>();
        this.add(new Chapter(0, MainPage.getLeftPage(), MainPage.getRightPage()));
    }

    public Chapters add(Chapter chapter) {
        this.chapterMap.put(chapter.chapterId, chapter);
        this.refreshButtons();
        return this;
    }

    public void refreshButtons() {
        this.chapterMap.forEach((chapterId, chapter) -> chapter.refreshButtons());
    }

    public Chapter current() {
        return this.chapterMap.get(currentChapterId);
    }

    public void next() {
        for (int i = this.currentChapterId + 1; i < this.chapterMap.size(); i++) {
            if (this.chapterMap.containsKey(i)) {
                this.currentChapterId = i;
                this.chapterMap.get(i).open();
            }
        }
    }

    public void prev() {
        for (int i = this.currentChapterId - 1; i > -1; i--) {
            if (this.chapterMap.containsKey(i)) {
                this.currentChapterId = i;
                this.chapterMap.get(i).open();
            }
        }
    }

    public boolean isFirst() {
        return currentChapterId == 0;
    }

    public boolean isLast() {
        for (int i = this.currentChapterId + 1; i < this.chapterMap.size(); i++) {
            if (this.chapterMap.containsKey(i)) {
                return false;
            }
        }
        return true;
    }
}

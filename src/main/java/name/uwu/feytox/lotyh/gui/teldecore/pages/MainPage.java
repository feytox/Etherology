package name.uwu.feytox.lotyh.gui.teldecore.pages;

import name.uwu.feytox.lotyh.util.LIdentifier;
import name.uwu.feytox.lotyh.gui.teldecore.TitleComponent;


public class MainPage extends DoublePage {
    public MainPage() {
        super(getLeftPage(), getRightPage());
    }

    public static EmptyPage getLeftPage() {
        EmptyPage leftPage = new EmptyPage(true, new LIdentifier("textures/gui/teldecore/teldecore_title_1.png"));
        leftPage.child(new TitleComponent("Телдекор")); // TODO: lang file
        return leftPage;
    }

    public static EmptyPage getRightPage() {
        EmptyPage rightPage = new EmptyPage(false, new LIdentifier("textures/gui/teldecore/teldecore_title_1.png"));
        rightPage.child(new TitleComponent("Содержание")); // TODO: lang file
        return rightPage;
    }
}

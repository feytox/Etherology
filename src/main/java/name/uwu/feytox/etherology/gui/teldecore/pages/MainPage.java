package name.uwu.feytox.etherology.gui.teldecore.pages;

import name.uwu.feytox.etherology.gui.teldecore.TitleComponent;
import name.uwu.feytox.etherology.util.EIdentifier;


public class MainPage extends DoublePage {
    public MainPage() {
        super(getLeftPage(), getRightPage());
    }

    public static EmptyPage getLeftPage() {
        EmptyPage leftPage = new EmptyPage(true, new EIdentifier("textures/gui/teldecore/teldecore_title_1.png"));
        leftPage.child(new TitleComponent("Телдекор")); // TODO: lang file
        return leftPage;
    }

    public static EmptyPage getRightPage() {
        EmptyPage rightPage = new EmptyPage(false, new EIdentifier("textures/gui/teldecore/teldecore_title_1.png"));
        rightPage.child(new TitleComponent("Содержание")); // TODO: lang file
        return rightPage;
    }
}

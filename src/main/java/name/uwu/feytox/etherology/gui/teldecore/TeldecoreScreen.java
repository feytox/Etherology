package name.uwu.feytox.etherology.gui.teldecore;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.*;
import name.uwu.feytox.etherology.gui.teldecore.pages.DoublePage;
import name.uwu.feytox.etherology.gui.teldecore.pages.EmptyPage;
import name.uwu.feytox.etherology.gui.teldecore.pages.MainPage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeldecoreScreen extends BaseOwoScreen<FlowLayout> {
    List<EmptyPage> pagesToOpen;

    public TeldecoreScreen(List<EmptyPage> pagesToOpen) {
        super();
        this.pagesToOpen = pagesToOpen;
    }


    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        rootComponent.child(new EmptyBook().child(
                getPage()
                        .positioning(Positioning.absolute(12, 7))
        ));
    }

    private VerticalFlowLayout getPage() {
        if (!this.pagesToOpen.isEmpty()) {
            return new DoublePage(pagesToOpen.get(0), pagesToOpen.get(1));
        }
        return new MainPage();
    }
}

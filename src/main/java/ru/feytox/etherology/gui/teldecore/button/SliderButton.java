package ru.feytox.etherology.gui.teldecore.button;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.page.ResearchTreePage;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.function.Consumer;

public class SliderButton extends AbstractButton {

    private static final Identifier SLIDER = EIdentifier.of("textures/gui/teldecore/icon/research_slider.png");

    private final Consumer<Float> deltaConsumer;
    private final float rootY;
    private boolean sliderDragged = false;

    public SliderButton(TeldecoreScreen parent, float rootX, float rootY, boolean active, Consumer<Float> deltaConsumer) {
        super(parent, SLIDER, null, rootX, rootY, 0, 0, 11, 13);
        this.active = active;
        this.deltaConsumer = deltaConsumer;
        this.rootY = rootY;
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        baseY = Math.clamp((float) mouseY - height/2f, rootY, rootY + ResearchTreePage.SLIDER_LENGTH);
        deltaConsumer.accept(baseY - rootY);
        sliderDragged = true;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!active || !sliderDragged) return false;
        baseY = Math.clamp((float) mouseY - height/2f, rootY, rootY + ResearchTreePage.SLIDER_LENGTH);
        deltaConsumer.accept(baseY - rootY);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean result = sliderDragged;
        sliderDragged = false;
        return result;
    }

    public void setDeltaY(float dy) {
        baseY = rootY + dy;
    }
}

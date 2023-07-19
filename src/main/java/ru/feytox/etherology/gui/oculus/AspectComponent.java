package ru.feytox.etherology.gui.oculus;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import ru.feytox.etherology.magic.aspects.EtherAspect;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class AspectComponent extends FlowLayout {
    public AspectComponent(EtherAspect aspect, int value) {
        super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

        TextureComponent aspectTexture = Components
                .texture(new EIdentifier("textures/gui/aspects.png"), aspect.getTextureMinX(), aspect.getTextureMinY(), 32, 32, 320, 192);
        LabelComponent valueComponent = Components.label(Text.of(String.valueOf(value))).shadow(true);

        this.child(aspectTexture.blend(true).sizing(Sizing.content())).child(valueComponent.positioning(Positioning.relative(95, 95)));
    }
}
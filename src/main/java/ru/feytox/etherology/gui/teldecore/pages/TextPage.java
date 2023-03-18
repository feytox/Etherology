package ru.feytox.etherology.gui.teldecore.pages;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextPage extends EmptyPage {

    public TextPage(boolean is_left, String title, String text) {
        super(is_left, new EIdentifier("textures/gui/teldecore/teldecore_title_2.png"));

        this.child(
                Components.label(Text.of(title))
                        .color(Color.ofRgb(6768690))
                        .positioning(Positioning.relative(50, 5))
        ).child(
                Components.label(Text.of(text))
                        .color(Color.BLACK)
                        .sizing(Sizing.fill(80), Sizing.fill(70))
                        .positioning(Positioning.relative(is_left ? 25 : 35, 40))
        );
    }

    public TextPage(boolean is_left, String text) {
        super(is_left, new EIdentifier("textures/gui/teldecore/teldecore_empty.png"));

        this.child(
                Components.label(Text.of(text))
                        .color(Color.BLACK)
                        .sizing(Sizing.fill(80), Sizing.fill(90))
                        .positioning(Positioning.relative(is_left ? 25 : 35, 15))
        );
    }

    public static List<TextPage> getTextPages(String title, String text) {
        // TODO: сделать разбиение текста на TextPage'ы
        List<String> stringPages = new ArrayList<>(Arrays.stream(text.split("\r")).toList());
        List<TextPage> pages = new ArrayList<>(List.of(new TextPage(true, title, stringPages.get(0))));
        stringPages.remove(0);
        boolean is_left_last = false;
        for (String s: stringPages) {
            pages.add(new TextPage(is_left_last, s));
            is_left_last = !is_left_last;
        }
        return pages;
    }
}

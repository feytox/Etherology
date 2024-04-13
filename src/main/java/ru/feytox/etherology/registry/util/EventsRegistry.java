package ru.feytox.etherology.registry.util;


import lombok.experimental.UtilityClass;
import ru.feytox.etherology.item.revelationView.RevelationViewRenderer;

@UtilityClass
public class EventsRegistry {

    public static void registerClientSide() {
        RevelationViewRenderer.registerRendering();
    }
}

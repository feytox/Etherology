package ru.feytox.etherology.registry.util;


import lombok.experimental.UtilityClass;
import ru.feytox.etherology.item.RevelationViewItem;

@UtilityClass
public class EventsRegistry {

    public static void registerClientSide() {
        RevelationViewItem.registerRendering();
    }
}

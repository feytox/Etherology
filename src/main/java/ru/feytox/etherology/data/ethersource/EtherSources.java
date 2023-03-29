package ru.feytox.etherology.data.ethersource;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class EtherSources {
    public static float getEtherFuel(Item item) {
        return EtherSourceLoader.INSTANCE.getEtherItems().getOrDefault(Registries.ITEM.getId(item), 0.0f);
    }

    public static boolean isEtherSource(Item item) {
        return EtherSourceLoader.INSTANCE.getEtherItems().containsKey(Registries.ITEM.getId(item));
    }
}

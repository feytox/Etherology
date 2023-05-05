package ru.feytox.etherology.util.deprecated;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

@Deprecated
public class FakeItem extends SimpleItem {
    public FakeItem(String itemId) {
        super(itemId, new FabricItemSettings());
    }
}

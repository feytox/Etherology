package name.uwu.feytox.etherology.util.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class FakeItem extends SimpleItem {
    public FakeItem(String itemId) {
        super(itemId, new FabricItemSettings());
    }
}

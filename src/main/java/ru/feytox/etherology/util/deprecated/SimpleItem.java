package ru.feytox.etherology.util.deprecated;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@Deprecated
public class SimpleItem extends Item {
    public String itemId;

    public SimpleItem(String itemId, Item.Settings settings) {
        super(settings);
        this.itemId = itemId;
    }

    // TODO: Custom ItemGroup
    public SimpleItem(String itemId) {
        this(itemId, new FabricItemSettings());
    }

    public SimpleItem register() {
        Registry.register(Registries.ITEM, new EIdentifier(this.itemId), this);
        return this;
    }

    public ItemStack asStack(int count) {
        ItemStack itemStack = this.getDefaultStack();
        itemStack.setCount(count);
        return itemStack;
    }

}
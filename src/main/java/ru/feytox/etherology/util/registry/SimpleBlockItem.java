package ru.feytox.etherology.util.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class SimpleBlockItem extends BlockItem {
    SimpleBlock block;

    public SimpleBlockItem(SimpleBlock block, Settings settings) {
        super(block, settings);
        this.block = block;
    }

    public SimpleBlockItem(SimpleBlock block) {
        this(block, new FabricItemSettings());
    }

    public SimpleBlockItem register() {
        Registry.register(Registries.ITEM, new EIdentifier(this.block.blockId), this);
        return this;
    }

    public ItemStack asStack(int count) {
        ItemStack itemStack = this.getDefaultStack();
        itemStack.setCount(count);
        return itemStack;
    }
}

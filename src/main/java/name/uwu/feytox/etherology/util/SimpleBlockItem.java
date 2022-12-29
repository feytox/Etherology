package name.uwu.feytox.etherology.util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class SimpleBlockItem extends BlockItem {
    SimpleBlock block;

    public SimpleBlockItem(SimpleBlock block, Settings settings) {
        super(block, settings);
        this.block = block;
    }

    public SimpleBlockItem(SimpleBlock block) {
        this(block, new FabricItemSettings().group(ItemGroup.MISC));
    }

    public SimpleBlockItem register() {
        Registry.register(Registry.ITEM, new EIdentifier(this.block.blockId), this);
        return this;
    }

    public ItemStack asStack(int count) {
        ItemStack itemStack = this.getDefaultStack();
        itemStack.setCount(count);
        return itemStack;
    }
}

package name.uwu.feytox.lotyh.util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
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
        Registry.register(Registry.ITEM, new LIdentifier(this.block.blockId), this);
        return this;
    }

    public ItemStack asStack(int count) {
        ItemStack itemStack = this.getDefaultStack();
        itemStack.setCount(count);
        return itemStack;
    }
}

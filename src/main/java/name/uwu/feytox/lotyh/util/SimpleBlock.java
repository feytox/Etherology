package name.uwu.feytox.lotyh.util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;


public abstract class SimpleBlock extends Block {
    String blockId;
    BlockItem blockItem;

    public SimpleBlock(String blockId, BlockItem blockItem, Block.Settings settings) {
        super(settings);
        this.blockId = blockId;
        this.blockItem = blockItem;
    }

    public SimpleBlock(String blockId, Block.Settings settings) {
        super(settings);
        this.blockId = blockId;
        this.blockItem = new SimpleBlockItem(this);
    }

    public SimpleBlock(BlockItem blockItem, String blockId) {
        this(blockId, FabricBlockSettings.of(Material.METAL).strength(4.0f));
    }

    public SimpleBlock registerAll() {
        this.registerBlock();
        this.registerItem();
        return this;
    }

    public SimpleBlock registerBlock() {
        Registry.register(Registry.BLOCK, new LIdentifier(this.blockId), this);
        return this;
    }

    // TODO: Custom ItemGroup
    public BlockItem registerItem() {
        return Registry.register(Registry.ITEM, new LIdentifier(this.blockId), this.blockItem);
    }

    public ItemStack asStack(int count) {
        ItemStack itemStack = this.blockItem.getDefaultStack();
        itemStack.setCount(count);
        return itemStack;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return super.getRaycastShape(state, world, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape(state, world, pos, context);
    }
}

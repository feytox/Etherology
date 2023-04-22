package ru.feytox.etherology.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.BlocksRegistry;
import ru.feytox.etherology.blocks.crate.CrateBlock;

public class CarriedCrateItem extends AliasedBlockItem {
    public CarriedCrateItem() {
        super(BlocksRegistry.CRATE, new FabricItemSettings());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult result = super.useOnBlock(context);
        if (result != ActionResult.FAIL) {
            PlayerEntity player = context.getPlayer();
            if (player != null && player.getAbilities().creativeMode) {
                player.getInventory().removeOne(context.getStack());
            }
        }
        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return;
        if (!entity.isPlayer()) return;

        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (!selected && player.getInventory().contains(stack)) {
            BlockPos blockPos = player.getBlockPos();
            boolean shouldFall = FallingBlock.canFallThrough(world.getBlockState(blockPos.down()));
            BlockState state = BlocksRegistry.CRATE.getDefaultState()
                    .with(CrateBlock.FALLING, shouldFall)
                    .with(CrateBlock.FACING, player.getHorizontalFacing().getOpposite());
            boolean canPlace = FallingBlock.canFallThrough(world.getBlockState(blockPos));
            boolean placeResult = false;
            if (canPlace) placeResult = world.setBlockState(player.getBlockPos(), state);

            if (placeResult) {
                BlockItem.writeNbtToBlockEntity(world, player, blockPos, stack);
            } else {
                NbtCompound data = stack.getOrCreateSubNbt("BlockEntityTag");
                DefaultedList<ItemStack> items = DefaultedList.ofSize(10, ItemStack.EMPTY);
                Inventories.readNbt(data, items);
                ItemScatterer.spawn(world, player.getBlockPos(), items);
            }
            stack.decrement(1);
        }
    }
}

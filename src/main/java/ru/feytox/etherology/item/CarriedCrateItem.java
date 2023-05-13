package ru.feytox.etherology.item;

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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.crate.CrateBlock;
import ru.feytox.etherology.registry.block.EBlocks;

public class CarriedCrateItem extends AliasedBlockItem {
    public CarriedCrateItem() {
        super(EBlocks.CRATE, new FabricItemSettings());
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
        if (!player.getInventory().contains(stack)) return;
        if (!selected) {
            BlockPos blockPos = player.getBlockPos();
            placeFallingCrate(world, blockPos, stack, player.getHorizontalFacing().getOpposite(), player);
        }
    }

    public static boolean placeFallingCrate(World world, BlockPos blockPos, ItemStack stack, Direction facing, @Nullable PlayerEntity player) {
        boolean shouldFall = FallingBlock.canFallThrough(world.getBlockState(blockPos.down()));
        BlockState state = EBlocks.CRATE.getDefaultState()
                .with(CrateBlock.FALLING, shouldFall)
                .with(CrateBlock.FACING, facing);
        boolean canPlace = FallingBlock.canFallThrough(world.getBlockState(blockPos));
        boolean placeResult = false;
        if (canPlace) placeResult = world.setBlockState(blockPos, state);

        if (placeResult) {
            BlockItem.writeNbtToBlockEntity(world, player, blockPos, stack);
        } else {
            NbtCompound data = stack.getOrCreateSubNbt("BlockEntityTag");
            DefaultedList<ItemStack> items = DefaultedList.ofSize(10, ItemStack.EMPTY);
            Inventories.readNbt(data, items);
            ItemScatterer.spawn(world, blockPos, items);
        }
        stack.decrement(1);
        return placeResult;
    }
}

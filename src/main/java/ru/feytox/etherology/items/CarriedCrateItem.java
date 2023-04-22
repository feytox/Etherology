package ru.feytox.etherology.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import ru.feytox.etherology.BlocksRegistry;

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

            HitResult hitResult = player.raycast(4.5, 0.0f, false);
            boolean placeResult = hitResult.getType() == HitResult.Type.BLOCK;
            if (placeResult) {
                // tries to place a crate
                placeResult = ActionResult.CONSUME == place(new ItemPlacementContext(world, player, player.getActiveHand(), stack, (BlockHitResult) hitResult));
            }

            if (!placeResult) {
                // when cannot to place a crate
                player.dropItem(stack.copy(), false);
                stack.decrement(1);
            } else {
                // when crate was placed successfully
                stack.decrement(1);
            }
        }
    }
}

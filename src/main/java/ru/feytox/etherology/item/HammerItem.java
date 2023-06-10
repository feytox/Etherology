package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

public class HammerItem extends SwordItem {
    private static boolean wasSelected = false;

    public HammerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed) {
        this(toolMaterial, attackDamage, attackSpeed, new FabricItemSettings());
    }

    public HammerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, FabricItemSettings itemSettings) {
        super(toolMaterial, attackDamage, attackSpeed, itemSettings);
    }

    /**
     * @param player player to check
     * @return true if player has hammer in main hand and offhand is empty
     */
    public static boolean checkHammer(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof HammerItem && player.getOffHandStack().isEmpty();
    }

    public static boolean checkHammer(IAnimatedPlayer animatedPlayer) {
        if (!(animatedPlayer instanceof AbstractClientPlayerEntity clientPlayer)) return false;
        return checkHammer(clientPlayer);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!(entity instanceof PlayerEntity player) || !world.isClient) return;
        if (player.getOffHandStack().isEmpty() || player.getOffHandStack().getItem() instanceof HammerItem) {
            wasSelected = player.getMainHandStack().getItem() instanceof HammerItem;
            return;
        }
        if (wasSelected == selected) return;

        wasSelected = player.getMainHandStack().getItem() instanceof HammerItem;
        if (selected) {
            player.sendMessage(Text.translatable("item.etherology.hammer.warn").formatted(Formatting.WHITE), true);
        }
    }
}

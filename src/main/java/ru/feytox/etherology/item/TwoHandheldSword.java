package ru.feytox.etherology.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public abstract class TwoHandheldSword extends SwordItem {
    private boolean wasSelected = false;

    public TwoHandheldSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public static <T extends TwoHandheldSword> boolean check(PlayerEntity player, Class<T> cls) {
        return cls.isInstance(player.getMainHandStack().getItem()) && player.getOffHandStack().isEmpty();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!(entity instanceof PlayerEntity player) || !world.isClient) return;
        if (player.getOffHandStack().isEmpty() || player.getOffHandStack().getItem() instanceof TwoHandheldSword) {
            wasSelected = player.getMainHandStack().getItem() instanceof TwoHandheldSword;
            return;
        }
        if (wasSelected == selected) return;

        wasSelected = player.getMainHandStack().getItem() instanceof TwoHandheldSword;
        if (selected) {
            player.sendMessage(Text.translatable("item.etherology.twohandheld.warn").formatted(Formatting.WHITE), true);
        }
    }
}

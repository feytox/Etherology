package ru.feytox.etherology.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NonNull;
import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.gui.staff.StaffLensesScreen;
import ru.feytox.etherology.magic.lens.LensMode;
import ru.feytox.etherology.registry.misc.KeybindsRegistry;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * @see ru.feytox.etherology.magic.staff.StaffComponent
 * @see ru.feytox.etherology.magic.lens.LensComponent
 */
public class StaffItem extends Item {

    public StaffItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        ItemStack staffStack = user.getStackInHand(hand);

        if (useLensEffect(world, user, staffStack, false, () -> hand)) return TypedActionResult.pass(staffStack);
        return TypedActionResult.fail(staffStack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.STAFF_ETHEROLOGY.getUseAction();
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        useLensEffect(world, user, stack, true, () -> getHandFromStack(user, stack));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    /**
     * @return True if pass or False if fail
     */
    private boolean useLensEffect(World world, LivingEntity user, ItemStack staffStack, boolean hold, Supplier<Hand> handGetter) {
        val lensStack = LensItem.getLensStack(staffStack);
        if (lensStack == null || !(lensStack.getItem() instanceof LensItem lensItem)) return false;

        val lensData = LensItem.getStaffLens(staffStack);
        if (lensData == null || lensData.isEmpty()) return false;
        val lensMode = lensData.getLensMode();

        if (lensMode.equals(LensMode.CHARGE)) return lensItem.onChargeUse(world, user, lensData, hold, handGetter);
        return lensItem.onStreamUse(world, user, lensData, hold, handGetter);
    }

    @Override
    public void onStoppedUsing(ItemStack staffStack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(staffStack, world, user, remainingUseTicks);

        val lensStack = LensItem.getLensStack(staffStack);
        if (lensStack == null || !(lensStack.getItem() instanceof LensItem lensItem)) return;

        val lensData = LensItem.getStaffLens(staffStack);
        if (lensData == null || lensData.isEmpty()) return;
        val lensMode = lensData.getLensMode();

        int holdTicks = getMaxUseTime(staffStack) - remainingUseTicks;
        Supplier<Hand> handGetter = () -> getHandFromStack(user, staffStack);
        if (lensMode.equals(LensMode.CHARGE)) lensItem.onChargeStop(world, user, lensData, holdTicks, handGetter);
        else lensItem.onStreamStop(world, user, lensData, holdTicks, handGetter);
    }

    private static void tickLensesMenu(@NonNull MinecraftClient client) {
        boolean isPressed = KeybindsRegistry.isPressed(KeybindsRegistry.STAFF_INTERACTION);
        boolean isSneakPressed = KeybindsRegistry.isPressed(client.options.sneakKey);

        if (!(client.currentScreen instanceof StaffLensesScreen screen)) {
            if (isSneakPressed) return;
            if (isPressed && client.currentScreen == null) {
                client.setScreen(new StaffLensesScreen(null));
            }
            return;
        }

        if (!isPressed) {
            screen.tryClose();
            return;
        }

        screen.tryOpen();
    }

    public static List<ItemStack> getPlayerLenses(@NonNull MinecraftClient client) {
        List<ItemStack> result = new ObjectArrayList<>();
        if (client.player == null) return result;

        PlayerInventory inventory = client.player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!(stack.getItem() instanceof LensItem lensItem)) continue;
            if (lensItem.isUnadjusted()) continue;
            result.add(stack);
        }

        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient) return;
        if (!entity.isPlayer()) return;
        MinecraftClient client = MinecraftClient.getInstance();

        ItemStack selectedStack = getStaffStackFromHand(entity);
        if (stack == null) {
            if (client.currentScreen instanceof StaffLensesScreen screen) screen.tryClose();
            return;
        }
        if (!stack.equals(selectedStack)) return;

        tickLensesMenu(client);
    }

    @Nullable
    public static ItemStack getStaffStackFromHand(Entity entity) {
        Iterator<ItemStack> stacks = entity.getHandItems().iterator();

        ItemStack result = null;
        while (stacks.hasNext()) {
            ItemStack handStack = stacks.next();
            if (!(handStack.getItem() instanceof StaffItem)) continue;
            if (result != null) return null;
            result = handStack;
        }

        return result;
    }

    @Nullable
    public static Hand getHandFromStack(LivingEntity entity, ItemStack stack) {
        for (Hand hand : Hand.values()) {
            if (entity.getStackInHand(hand).equals(stack)) return hand;
        }
        return null;
    }
}

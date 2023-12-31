package ru.feytox.etherology.item;

import lombok.NonNull;
import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.gui.staff.LensSelectionType;
import ru.feytox.etherology.gui.staff.StaffLensesScreen;
import ru.feytox.etherology.magic.lense.LensMode;
import ru.feytox.etherology.magic.staff.StaffLenses;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.network.interaction.StaffMenuSelectionC2S;
import ru.feytox.etherology.registry.util.EtherologyComponents;
import ru.feytox.etherology.registry.util.KeybindsRegistry;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class StaffItem extends Item {

    public StaffItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        ItemStack staffStack = user.getStackInHand(hand);

        if (useLenseEffect(world, user, staffStack, false, () -> hand)) return TypedActionResult.pass(staffStack);
        return TypedActionResult.fail(staffStack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.STAFF_ETHEROLOGY.getUseAction();
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        useLenseEffect(world, user, stack, true, () -> getHandFromStack(user, stack));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    /**
     * @return True if pass or False if fail
     */
    private boolean useLenseEffect(World world, LivingEntity user, ItemStack staffStack, boolean hold, Supplier<Hand> handGetter) {
        val staff = EtherologyComponents.STAFF.get(staffStack);
        val partInfo = staff.getPartInfo(StaffPart.LENS);
        if (partInfo == null) return false;

        val lensPattern = partInfo.getFirstPattern();
        if (!(lensPattern instanceof StaffLenses lensType)) return false;
        if (!(lensType.getLensItem() instanceof LensItem lensItem)) return false;

        val lensData = EtherologyComponents.LENS.get(staffStack);
        if (lensData.isEmpty()) return false;
        val lensMode = lensData.getLensMode();

        if (lensMode.equals(LensMode.CHARGE)) return lensItem.onChargeUse(world, user, lensData, hold, handGetter);
        return lensItem.onStreamUse(world, user, lensData, hold, handGetter);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
        val staff = EtherologyComponents.STAFF.get(stack);
        val partInfo = staff.getPartInfo(StaffPart.LENS);
        if (partInfo == null) return;

        val lensPattern = partInfo.getFirstPattern();
        if (!(lensPattern instanceof StaffLenses lensType)) return;
        if (!(lensType.getLensItem() instanceof LensItem lensItem)) return;

        val lensData = EtherologyComponents.LENS.get(stack);
        if (lensData.isEmpty()) return;
        val lensMode = lensData.getLensMode();

        int holdTicks = getMaxUseTime(stack) - remainingUseTicks;
        Supplier<Hand> handGetter = () -> getHandFromStack(user, stack);
        if (lensMode.equals(LensMode.CHARGE)) lensItem.onChargeStop(world, user, lensData, holdTicks, handGetter);
        else lensItem.onStreamStop(world, user, lensData, holdTicks, handGetter);
    }

    private static void tickLensesMenu(@NonNull MinecraftClient client) {
        boolean isOpened = client.currentScreen instanceof StaffLensesScreen;

        if (!client.options.getPerspective().isFirstPerson() || !KeybindsRegistry.isPressed(KeybindsRegistry.OPEN_LENSE_MENU)) {
            if (isOpened) {
                checkSelectedLense(client, (StaffLensesScreen) client.currentScreen);
                client.currentScreen.close();
            }
            return;
        }

        if (isOpened) return;

        client.setScreen(new StaffLensesScreen(client.currentScreen));
    }

    private static void checkSelectedLense(@NonNull MinecraftClient client, StaffLensesScreen lensesScreen) {
        List<ItemStack> stacks = StaffLensesScreen.getPlayerLenses(client);

        val selected = lensesScreen.getSelected();
        if (selected.equals(LensSelectionType.NONE)) return;
        if (!selected.isEmptySelectedItem() && stacks.isEmpty()) return;
        int selectedItemId = lensesScreen.getChosenItem();

        ItemStack selectedStack = selectedItemId == -1 || selected.isEmptySelectedItem() ? ItemStack.EMPTY : stacks.get(selectedItemId);
        val packet = new StaffMenuSelectionC2S(selected, selectedStack);
        packet.sendToServer();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient) return;
        if (!entity.isPlayer()) return;
        MinecraftClient client = MinecraftClient.getInstance();

        ItemStack selectedStack = getStaffStackFromHand(entity);
        if (stack == null) {
            if (client.currentScreen instanceof StaffLensesScreen) client.currentScreen.close();
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

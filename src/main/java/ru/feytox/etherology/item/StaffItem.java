package ru.feytox.etherology.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.gui.staff.StaffLensesScreen;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.registry.misc.KeybindsRegistry;
import ru.feytox.etherology.util.misc.ItemComponent;
import ru.feytox.etherology.util.misc.ItemData;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

/**
 * @see StaffComponent
 * @see LensComponent
 */
public class StaffItem extends Item {

    public StaffItem() {
        super(new Settings().maxCount(1).component(ComponentTypes.STAFF, StaffComponent.DEFAULT));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        ItemStack staffStack = user.getStackInHand(hand);

        useLensEffect(world, user, staffStack, false, () -> hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(staffStack);
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
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    private void useLensEffect(World world, LivingEntity user, ItemStack staffStack, boolean hold, Supplier<Hand> handGetter) {
        ItemStack lensStack = LensItem.getStaffLens(staffStack);
        if (lensStack == null || !(lensStack.getItem() instanceof LensItem lensItem)) return;

        ItemData<LensComponent> lensData = LensComponent.getWrapper(lensStack).orElse(null);
        if (lensData == null) return;
        if (!EtherComponent.isEnough(user, lensItem.getEtherCost(lensData.getComponent()))) return;

        boolean isDamaged = switch (lensData.getComponent().mode()) {
            case CHARGE -> lensItem.onChargeUse(world, user, lensData, lensStack, hold, handGetter);
            case STREAM -> lensItem.onStreamUse(world, user, lensData, lensStack, hold, handGetter);
        };

        if (!isDamaged) return;
        onLensDamage(world, user, staffStack, lensItem, lensStack);
    }

    @Override
    public void onStoppedUsing(ItemStack staffStack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(staffStack, world, user, remainingUseTicks);

        ItemStack lensStack = LensItem.getStaffLens(staffStack);
        if (lensStack == null || !(lensStack.getItem() instanceof LensItem lensItem)) return;

        ItemData<LensComponent> lensData = LensComponent.getWrapper(lensStack).orElse(null);
        if (lensData == null) return;

        int holdTicks = getMaxUseTime(staffStack, user) - remainingUseTicks;
        Supplier<Hand> handGetter = () -> getHandFromStack(user, staffStack);

        boolean isDamaged = switch (lensData.getComponent().mode()) {
            case CHARGE -> {
                int chargeTime = lensItem.getChargeTime(lensData.getComponent(), holdTicks);
                yield lensItem.onChargeStop(world, user, lensData, lensStack, chargeTime, handGetter);
            }
            case STREAM -> lensItem.onStreamStop(world, user, lensData, lensStack, holdTicks, handGetter);
        };

        if (!isDamaged) return;
        onLensDamage(world, user, staffStack, lensItem, lensStack);
    }

    private static void onLensDamage(World world, LivingEntity user, ItemStack staffStack, LensItem lensItem, ItemStack lensStack) {
        if (!lensStack.isEmpty()) {
            LensItem.placeLensOnStaff(staffStack, lensStack);
            return;
        }

        LensItem.takeLensFromStaff(staffStack);
        if (!(world instanceof ServerWorld serverWorld)) return;

        Vec3d pos = new Vec3d(user.getX(), user.getEyeY(), user.getZ());
        LensItem.playLensBrakeSound(serverWorld, pos);
        LensItem.spawnLensBrakeParticles(serverWorld, lensItem, pos, user.getPitch(), user.getYaw());
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
        if (!(entity instanceof PlayerEntity player)) return;
        MinecraftClient client = MinecraftClient.getInstance();

        ItemStack selectedStack = getStaffInHands(player);
        if (stack == null) {
            if (client.currentScreen instanceof StaffLensesScreen screen) screen.tryClose();
            return;
        }
        if (!stack.equals(selectedStack)) return;

        tickLensesMenu(client);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    public static boolean isStaffInHand(LivingEntity entity) {
        return StreamSupport.stream(entity.getHandItems().spliterator(), false)
                .anyMatch(stack -> stack.getItem() instanceof StaffItem);
    }

    @Nullable
    public static ItemStack getStaffInHands(LivingEntity entity) {
        return StreamSupport.stream(entity.getHandItems().spliterator(), false)
                .filter(stack -> stack.getItem() instanceof StaffItem)
                .findFirst().orElse(null);
    }

    @Nullable
    public static Hand getHandFromStack(LivingEntity entity, ItemStack stack) {
        for (Hand hand : Hand.values()) {
            if (entity.getStackInHand(hand).equals(stack)) return hand;
        }
        return null;
    }

    public static void setLensComponent(ItemStack staffStack, ItemStack lensStack) {
        staffStack.set(ComponentTypes.STAFF_LENS, new ItemComponent(lensStack));
    }
}

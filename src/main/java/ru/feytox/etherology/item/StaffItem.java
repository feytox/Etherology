package ru.feytox.etherology.item;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.staff.LensSelectionType;
import ru.feytox.etherology.gui.staff.StaffLensesScreen;
import ru.feytox.etherology.magic.staff.*;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.interaction.StaffMenuSelectionC2S;
import ru.feytox.etherology.registry.util.KeybindsRegistry;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StaffItem extends Item {

    public static final ImmutableList<StaffPartInfo> DEFAULT_STAFF;

    public StaffItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    public static void writeDefaultParts(NbtCompound stackNbt) {
        NbtCompound parts = new NbtCompound();

        DEFAULT_STAFF.forEach(partInfo -> {
                    NbtCompound nbt = new NbtCompound();
                    nbt.put(StaffPartInfo.NBT_KEY, partInfo);
                    parts.put(partInfo.getPart().getName(), nbt);
                });

        stackNbt.put("parts", parts);
    }

    public static boolean setPartInfo(ItemStack stack, StaffPart part, StaffPattern firstPattern, StaffPattern secondPattern) {
        val staffData = readNbtSafe(stack);
        if (staffData == null) return false;

        StaffPartInfo partInfo = new StaffPartInfo(part, firstPattern, secondPattern);
        staffData.put(part, partInfo);
        writeNbt(stack, staffData);
        return true;
    }

    @Nullable
    public static StaffPartInfo getPartInfo(ItemStack stack, StaffPart part) {
        val staffData = readNbtSafe(stack);
        if (staffData == null) return null;

        return staffData.getOrDefault(part, null);
    }

    public static void removePartInfo(ItemStack stack, StaffPart part) {
        val staffData = readNbtSafe(stack);
        if (staffData == null) return;

        staffData.remove(part);
        writeNbt(stack, staffData);
    }

    public static void writeNbt(ItemStack stack, Map<StaffPart, StaffPartInfo> parts) {
        NbtCompound stackNbt = stack.getOrCreateNbt();
        NbtCompound partsNbt = new NbtCompound();

        parts.forEach((part, partInfo) -> {
            NbtCompound nbt = new NbtCompound();
            nbt.put(StaffPartInfo.NBT_KEY, partInfo);
            partsNbt.put(partInfo.getPart().getName(), nbt);
        });

        stackNbt.put("parts", partsNbt);
        stack.setNbt(stackNbt);
    }

    @Nullable
    private static Map<StaffPart, StaffPartInfo> readNbtSafe(ItemStack stack) {
        val staffData = readNbt(stack);
        if (staffData == null) {
            Etherology.ELOGGER.error("Null staff data after staff nbt reading");
            return null;
        }
        return staffData;
    }

    @Nullable
    public static Map<StaffPart, StaffPartInfo> readNbt(ItemStack stack) {
        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt == null || stackNbt.isEmpty()) return null;

        NbtCompound partsNbt = stackNbt.getCompound("parts");
        if (partsNbt.isEmpty()) return null;
        return partsNbt.getKeys().stream()
                .map(partsNbt::getCompound)
                .map(nbt -> {
                    try {
                        return nbt.get(StaffPartInfo.NBT_KEY);
                    } catch (Exception e) {
                        Etherology.ELOGGER.error("Found non-PartInfo element while loading EtherStaff NBT");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(StaffPartInfo::getPart, part -> part));
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
        EtherologyNetwork.sendToServer(packet);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!entity.isPlayer() || !world.isClient) return;
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

    static {
        DEFAULT_STAFF = ImmutableList.of(
                new StaffPartInfo(StaffPart.CORE, StaffMaterial.OAK, StaffPattern.EMPTY),
                new StaffPartInfo(StaffPart.HEAD, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffPart.DECOR, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffPart.TIP, StaffStyles.TRADITIONAL, StaffMetals.IRON)
        );
    }
}

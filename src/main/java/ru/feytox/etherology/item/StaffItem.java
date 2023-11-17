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
import org.joml.Vector2d;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.staff.StaffLensesScreen;
import ru.feytox.etherology.magic.staff.*;
import ru.feytox.etherology.registry.util.KeybindsRegistry;

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

    public static void setPartInfo(ItemStack stack, StaffPart part, StaffPattern firstPattern, StaffPattern secondPattern) {
        val staffData = readNbt(stack);
        if (staffData == null) {
            Etherology.ELOGGER.error("Null staff data after staff nbt reading");
            return;
        }

        StaffPartInfo partInfo = new StaffPartInfo(part, firstPattern, secondPattern);
        staffData.put(part, partInfo);
        writeNbt(stack, staffData);
    }

    public static void removePartInfo(ItemStack stack, StaffPart part) {
        val staffData = readNbt(stack);
        if (staffData == null) {
            Etherology.ELOGGER.error("Null staff data after staff nbt reading");
            return;
        }

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

    private static void checkSelectedLense(MinecraftClient client, StaffLensesScreen lensesScreen) {
        double mouseX = client.mouse.getX();
        double mouseY = client.mouse.getY();

        double centerX = client.getWindow().getWidth() / 2d;
        double centerY = client.getWindow().getHeight() / 2d;

        Vector2d mouseVec = new Vector2d(mouseX - centerX, mouseY - centerY);
        double len = mouseVec.lengthSquared();
        if (len > 200 * 200 || len < 100 * 100) return;

        List<ItemStack> stacks = StaffLensesScreen.getPlayerLenses(client);
        if (stacks.isEmpty()) return;

        int chosenItemId = lensesScreen.getChosenItem();
        if (chosenItemId == -1) return;
        ItemStack result = stacks.get(chosenItemId);

        // TODO: 14.11.2023 packet send
        client.player.sendMessage(result.getName());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!entity.isPlayer() || !world.isClient) return;
        MinecraftClient client = MinecraftClient.getInstance();

        if (!selected) {
            if (client.currentScreen instanceof StaffLensesScreen) client.currentScreen.close();
            return;
        }

        tickLensesMenu(client);
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

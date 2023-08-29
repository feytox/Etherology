package ru.feytox.etherology.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.magic.staff.StaffStyle;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EtherStaff extends Item {
    public EtherStaff() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NbtCompound stackNbt = stack.getOrCreateNbt();
        writeDefaultParts(stackNbt);
        stack.setNbt(stackNbt);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return;
        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt == null) stackNbt = new NbtCompound();
        if (stackNbt.has(StaffPartInfo.LIST_KEY)) return;

        writeDefaultParts(stackNbt);
        stack.setNbt(stackNbt);
    }

    private static void writeDefaultParts(NbtCompound stackNbt) {
        // TODO: 29.08.2023 replace hard code with default values
        List<StaffStyle> staffStyles = Arrays.stream(StaffStyle.values()).filter(style -> !style.equals(StaffStyle.NULL)).collect(Collectors.toCollection(ObjectArrayList::new));
        Random random = new Random();

        List<NbtCompound> parts = Arrays.stream(StaffPart.values()).filter(staffPart -> !staffPart.equals(StaffPart.NULL))
                .map(staffPart -> {
                    if (staffPart.isHardCode()) return StaffPartInfo.of(staffPart, StaffStyle.NULL);
                    return StaffPartInfo.of(staffPart, staffStyles.get(random.nextInt(staffStyles.size())));
                })
                .map(staffInfo -> {
                    NbtCompound nbt = new NbtCompound();
                    staffInfo.writeNbt(nbt);
                    return nbt;
                }).collect(Collectors.toCollection(ObjectArrayList::new));

        NbtList nbtList = new NbtList();
        nbtList.addAll(parts);
        stackNbt.put(StaffPartInfo.LIST_KEY, nbtList);
    }
}

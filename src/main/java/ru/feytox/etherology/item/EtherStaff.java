package ru.feytox.etherology.item;

import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.staff.StaffParts;
import ru.feytox.etherology.magic.staff.StaffPartsInfo;
import ru.feytox.etherology.magic.staff.StaffPattern;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

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
        if (stackNbt.has(StaffPartsInfo.LIST_KEY)) return;

        writeDefaultParts(stackNbt);
        stack.setNbt(stackNbt);
    }

    private static void writeDefaultParts(NbtCompound stackNbt) {
        // TODO: 02.09.2023 replace with default values
        Random random = new Random();
        NbtList nbtList = new NbtList();

        Arrays.stream(StaffParts.values())
                .map(part -> new StaffPartsInfo(part, getRandomPattern(random, part.getFirstPatterns()), getRandomPattern(random, part.getSecondPatterns())))
                .map(partsInfo -> {
                    NbtCompound nbt = new NbtCompound();
                    nbt.put(StaffPartsInfo.NBT_KEY, partsInfo);
                    return nbt;
                })
                .forEach(nbtList::add);

        stackNbt.put(StaffPartsInfo.LIST_KEY, nbtList);
    }

    private static StaffPattern getRandomPattern(Random random, Supplier<List<? extends StaffPattern>> patternsGetter) {
        val patterns = patternsGetter.get();
        return patterns.get(random.nextInt(patterns.size()));
    }
}

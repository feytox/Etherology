package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.spill_barrel.SpillBarrelBlockEntity;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.List;

public class SpillBarrelItem extends BlockItem {
    public SpillBarrelItem() {
        super(EBlocks.SPILL_BARREL, new FabricItemSettings());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtList items = stack.getOrCreateSubNbt("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE);
        NbtCompound itemCompound = items.getCompound(0);
        ItemStack potionStack = ItemStack.fromNbt(itemCompound);

        MutableText resultText = Text.translatable("lore.etherology.spill_barrel.empty").formatted(Formatting.GRAY);
        if (!potionStack.isEmpty()) {
            int potionCount = 0;
            for (int i = 0; i < 16; i++) {
                NbtCompound compound = items.getCompound(i);
                if (!ItemStack.fromNbt(compound).isEmpty()) potionCount++;
            }

            resultText = SpillBarrelBlockEntity.getPotionInfo(potionStack, potionCount, false, Text.empty()).formatted(Formatting.GRAY);
        }

        tooltip.add(1, resultText);
    }
}

package ru.feytox.etherology.block.spill_barrel;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Setter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

public class SpillBarrelBlockEntity extends TickableBlockEntity implements ImplementedInventory, SidedInventory, Nameable {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);
    @Setter
    private Text customName;

    public SpillBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(EBlocks.SPILL_BARREL_BLOCK_ENTITY, pos, state);
    }

    /**
     * @param handStack ItemStack to be filled in the SpillBarrel
     * @return true if the ItemStack can be filled in the SpillBarrel
     */
    public boolean tryFillBarrel(ItemStack handStack) {
        if (!handStack.isOf(Items.POTION)) return false;

        if (isEmpty()) {
            items.set(0, handStack);
            markDirty();
            return true;
        }


        val barrelPotion = items.getFirst().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion().orElse(null);
        val stackContent = handStack.get(DataComponentTypes.POTION_CONTENTS);
        if (stackContent == null || barrelPotion == null) return false;
        if (!stackContent.matches(barrelPotion) || !items.get(15).isEmpty()) return false;

        for (int i = 0; i < 16; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, handStack);
                markDirty();
                return true;
            }
        }

        return false;
    }

    /**
     * @param handStack ItemStack to be emptied from the SpillBarrel
     * @return the ItemStack that was emptied or not from the SpillBarrel
     */
    public ItemStack tryEmptyBarrel(ItemStack handStack) {
        if (!handStack.isOf(Items.GLASS_BOTTLE) || isEmpty()) return handStack;

        val barrelContent = items.getFirst().get(DataComponentTypes.POTION_CONTENTS);
        if (barrelContent == null) return handStack;

        for (int i = 15; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack outputStack = Items.POTION.getDefaultStack();
                outputStack.set(DataComponentTypes.POTION_CONTENTS, barrelContent);
                if (hasCustomName()) outputStack.set(DataComponentTypes.CUSTOM_NAME, getCustomName());
                items.set(i, ItemStack.EMPTY);
                markDirty();
                return outputStack;
            }
        }
        return handStack;
    }

    /**
     * @param player Player to show the info about the SpillBarrel
     */
    public void showPotionsInfo(PlayerEntity player) {
        Text resultText = Text.translatable("lore.etherology.spill_barrel.empty").formatted(Formatting.GRAY);
        if (!isEmpty()) {
            MutableText potionInfo = getPotionInfo(items.getFirst(), getPotionCount(), hasCustomName(), getCustomName());
            if (potionInfo != null) resultText = potionInfo.formatted(Formatting.GRAY);
        }

        player.sendMessage(resultText, true);
    }

    @Nullable
    public static MutableText getPotionInfo(ItemStack potionStack, long potionCount, boolean withCustomName, Text customName) {
        val barrelContent = potionStack.get(DataComponentTypes.POTION_CONTENTS);
        if (barrelContent == null) return null;

        StatusEffectInstance statusEffectInstance = barrelContent.getEffects().iterator().next();
        Text effectText = Text.translatable(statusEffectInstance.getTranslationKey());
        Text levelText = Text.translatable("potion.potency." + statusEffectInstance.getAmplifier());
        if (!levelText.getString().isEmpty()) {
            levelText = Text.of(" " + levelText.getString());
        }

        if (withCustomName) return Text.translatable("lore.etherology.spill_barrel.filled", customName, "", potionCount);

        return Text.translatable("lore.etherology.spill_barrel.filled",
                effectText.getString(), levelText.getString(), potionCount);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public int getPotionCount() {
        int count = 0;
        for (int i = 0; i < 16; i++) {
            if (!items.get(i).isEmpty()) count++;
        }
        return count;
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(items));
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(items);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, items, registryLookup);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        items.clear();
        Inventories.readNbt(nbt, items, registryLookup);
    }

    @Override
    public Text getName() {
        return hasCustomName() ? customName : Text.translatable("block.etherology.spill_barrel");
    }

    @Nullable
    @Override
    public Text getCustomName() {
        return customName;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}

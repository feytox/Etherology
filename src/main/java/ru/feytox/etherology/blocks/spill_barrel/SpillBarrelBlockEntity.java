package ru.feytox.etherology.blocks.spill_barrel;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.BlocksRegistry;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

public class SpillBarrelBlockEntity extends TickableBlockEntity implements ImplementedInventory, Nameable {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);
    private Text customName;

    public SpillBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.SPILL_BARREL_BLOCK_ENTITY, pos, state);
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

        Potion barrelPotion = PotionUtil.getPotion(items.get(0));
        Potion stackPotion = PotionUtil.getPotion(handStack);
        if (stackPotion.equals(barrelPotion) && items.get(15).isEmpty()) {
            for (int i = 0; i < 16; i++) {
                if (items.get(i).isEmpty()) {
                    items.set(i, handStack);
                    markDirty();
                    return true;
                }
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

        Potion barrelPotion = PotionUtil.getPotion(items.get(0));
        for (int i = 15; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack outputStack = PotionUtil.setPotion(Items.POTION.getDefaultStack(), barrelPotion);
                if (hasCustomName()) outputStack.setCustomName(getCustomName());
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
            resultText = getPotionInfo(items.get(0), getPotionCount(), hasCustomName(), getCustomName()).formatted(Formatting.GRAY);
        }

        player.sendMessage(resultText, true);
    }

    public static MutableText getPotionInfo(ItemStack potionStack, int potionCount, boolean withCustomName, Text customName) {
        Potion barrelPotion = PotionUtil.getPotion(potionStack);
        StatusEffectInstance statusEffectInstance = barrelPotion.getEffects().get(0);
        Text effectText = Text.translatable(statusEffectInstance.getTranslationKey());
        Text levelText = Text.translatable("potion.potency." + statusEffectInstance.getAmplifier());
        if (!levelText.getString().equals("")) {
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
        for (int i = 0; i < 15; i++) {
            if (!items.get(i).isEmpty()) count++;
        }
        return count;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
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
}

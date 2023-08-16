package ru.feytox.etherology.block.pedestal;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.DyedCarpetBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.ItemAspectsLoader;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.magic.aspects.EtherAspectsProvider;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.feyapi.UniqueProvider;

import static ru.feytox.etherology.registry.block.EBlocks.PEDESTAL_BLOCK_ENTITY;

public class PedestalBlockEntity extends TickableBlockEntity implements ImplementedInventory, EtherAspectsProvider, UniqueProvider {
    // 0 - item, 1 - carpet
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    @Getter
    @Setter
    private Float cachedUniqueOffset = null;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void interact(ServerWorld world, BlockState state, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        ItemStack pedestalStack = getStack(0);
        ItemStack carpetStack = getStack(1);

        if (!handStack.isEmpty()) {
            // размещение ковра
            if (placeCarpet(world, state, player, hand, handStack, carpetStack)) return;

            // размещение предмета на пустом пьедестале
            if (pedestalStack.isEmpty()) {
                setStack(0, handStack.copyWithCount(1));
                handStack.decrement(1);
                player.setStackInHand(hand, handStack);
                return;
            }

            // взятие похожего предмета с пьедестала
            if (ItemStack.canCombine(handStack, pedestalStack) && handStack.getCount() < handStack.getMaxCount()) {
                setStack(0, ItemStack.EMPTY);
                handStack.increment(1);
                player.setStackInHand(hand, handStack);
                return;
            }
        }

        // взятие ковра в пустую руку
        if (pedestalStack.isEmpty()) {
            if (carpetStack.isEmpty()) return;

            player.setStackInHand(hand, carpetStack);
            setStack(1, ItemStack.EMPTY);
            setCarpetColor(world, state, DyeColor.WHITE, false);
            return;
        }

        // взятие предмета в пустую руку
        player.setStackInHand(hand, pedestalStack);
        setStack(0, ItemStack.EMPTY);
    }

    private boolean placeCarpet(ServerWorld world, BlockState state, PlayerEntity player, Hand hand, ItemStack handStack, ItemStack carpetStack) {
        if (!(handStack.getItem() instanceof BlockItem blockItem)) return false;
        if (!(blockItem.getBlock() instanceof DyedCarpetBlock carpet)) return false;

        // взятие ковра в стак с коврами
        if (ItemStack.canCombine(handStack, carpetStack) && handStack.getCount() < handStack.getMaxCount()) {
            setStack(1, ItemStack.EMPTY);
            handStack.increment(1);
            player.setStackInHand(hand, handStack);
            setCarpetColor(world, state, DyeColor.WHITE, false);
            return true;
        }

        ItemStack copyStack = handStack.copyWithCount(1);
        if (handStack.getCount() > 1) {
            if (!carpetStack.isEmpty()) return true;

            // размещение 1 ковра из стака с коврами
            setStack(1, copyStack);
            handStack.decrement(1);
            player.setStackInHand(hand, handStack);
            setCarpetColor(world, state, carpet.getDyeColor(), true);
            return true;
        }

        // замена ковра (или пустоты) на пьедестале ковром из руки
        player.setStackInHand(hand, carpetStack);
        setStack(1, copyStack);
        setCarpetColor(world, state, carpet.getDyeColor(), true);
        return true;
    }

    private void setCarpetColor(ServerWorld world, BlockState state, DyeColor dyeColor, boolean withCarpet) {
        world.setBlockState(pos, state.with(PedestalBlock.CLOTH_COLOR, dyeColor).with(PedestalBlock.DECORATION, withCarpet));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        nbt.putBoolean("removed", removed);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        items.clear();
        Inventories.readNbt(nbt, items);
        removed = nbt.getBoolean("removed");
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public @Nullable EtherAspectsContainer getStoredAspects() {
        return ItemAspectsLoader.getAspectsOf(items.get(0).getItem()).orElse(null);
    }

    @Override
    public Text getAspectsSourceName() {
        String pedestalText = Text.translatable(getCachedState().getBlock().getTranslationKey()).getString();
        String itemText = items.get(0).getName().getString();
        return Text.of(pedestalText + " (" + itemText + ")");
    }
}

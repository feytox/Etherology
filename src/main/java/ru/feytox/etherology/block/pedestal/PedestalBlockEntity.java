package ru.feytox.etherology.block.pedestal;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
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

    public void interact(ServerWorld world, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        if (this.isEmpty() && !handStack.isEmpty()) {
            ItemStack copyStack = handStack.copy();
            copyStack.setCount(1);
            this.setStack(0, copyStack);
            handStack.decrement(1);
        } else if (!this.isEmpty() && (handStack.isEmpty() || handStack.isItemEqual(this.items.get(0)))) {
            ItemStack pedestalStack = this.items.get(0);
            this.clear();
            if (handStack.isItemEqual(pedestalStack)) {
                handStack.increment(1);
                syncData(world);
                return;
            }
            player.setStackInHand(hand, pedestalStack);
        }
        syncData(world);
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

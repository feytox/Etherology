package name.uwu.feytox.lotyh.blocks.pedestal;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.lotyh.mixin.ItemEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static name.uwu.feytox.lotyh.BlocksRegistry.PEDESTAL_BLOCK_ENTITY;

public class PedestalBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private UUID displayedItemUUID = null;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void interact(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        if (this.isEmpty() && !handStack.isEmpty()) {
            ItemStack copyStack = handStack.copy();
            copyStack.setCount(1);
            this.setStack(0, copyStack);
            handStack.decrement(1);
            markDirty();
        } else if (!this.isEmpty() && (handStack.isEmpty() || handStack.isItemEqual(this.items.get(0)))) {
            ItemStack pedestalStack = this.items.get(0);
            this.clear();
            if (handStack.isItemEqual(pedestalStack)) {
                handStack.increment(1);
                return;
            }
            player.setStackInHand(hand, pedestalStack);
            markDirty();
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, PedestalBlockEntity blockEntity) {
        if (!world.isClient) {
            blockEntity.tickItem((ServerWorld) world);
        }
    }

    public void tickItem(ServerWorld world) {
        ItemEntity displayedItem = getDisplayedItemEntity(world);

        if (displayedItem != null && displayedItem.isAlive()) {
            displayedItem.setVelocity(0.0, 0.0, 0.0);
            if (displayedItem.getX() != this.pos.getX()+0.5 ||
            displayedItem.getY() != this.pos.getY()+1.0 ||
            displayedItem.getZ() != this.pos.getZ()+0.5) {
                displayedItem.setPos(this.pos.getX()+0.5, this.pos.getY()+1.0, this.pos.getZ()+0.5);
            }
        }

        if (!this.isEmpty() && (displayedItem == null || !displayedItem.isAlive())) {
            ItemStack displayedItemStack = items.get(0).copy();
            displayedItemStack.setCount(1);
            displayedItem = new ItemEntity(world, this.pos.getX()+0.5, this.pos.getY()+1.0, this.pos.getZ()+0.5,
                    displayedItemStack, 0.0, 0.0, 0.0);
            ((ItemEntityAccessor) displayedItem).setItemAge(-32768);
            ((ItemEntityAccessor) displayedItem).setPickupDelay(32767);
            displayedItem.setInvulnerable(true);
            world.spawnEntity(displayedItem);
            displayedItemUUID = displayedItem.getUuid();
        } else if (this.isEmpty() && displayedItem != null) {
            if (displayedItem.isAlive()) displayedItem.kill();
            displayedItemUUID = null;
        }
    }

    public void onBreak(World world) {
        ItemEntity displayedItem = getDisplayedItemEntity((ServerWorld) world);
        if (displayedItem != null && displayedItem.isAlive()) displayedItem.kill();
    }

    @Nullable
    public ItemEntity getDisplayedItemEntity(ServerWorld world) {
        Entity searchedEntity = world.getEntity(displayedItemUUID);
        return searchedEntity instanceof ItemEntity ? (ItemEntity) searchedEntity : null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        displayedItemUUID = displayedItemUUID == null ? UUID.randomUUID() : displayedItemUUID;
        nbt.putUuid("displayedItemUUID", displayedItemUUID);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        displayedItemUUID = nbt.getUuid("displayedItemUUID");
        Inventories.readNbt(nbt, items);
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
}

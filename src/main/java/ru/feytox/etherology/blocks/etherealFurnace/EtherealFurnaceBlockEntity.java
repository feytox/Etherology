package ru.feytox.etherology.blocks.etherealFurnace;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.ethersource.EtherSources;
import ru.feytox.etherology.magic.ether.EtherCounter;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.Collections;
import java.util.List;

import static ru.feytox.etherology.BlocksRegistry.ETHEREAL_FURNACE_BLOCK_ENTITY;
import static ru.feytox.etherology.blocks.etherealFurnace.EtherealFurnace.LIT;

public class EtherealFurnaceBlockEntity extends TickableBlockEntity
        implements EtherStorage, ImplementedInventory, NamedScreenHandlerFactory, EtherCounter {
    public static final int MAX_FUEL = 8;
    // TODO: 28/03/2023 спросить, точно ли одинаково у всех
    private static final int DEFAULT_COOK_TIME = 20*10;
    private float storedEther;
    // 0 - fuel, 1 - item, 2 - ether
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int fuel;
    private int cookTime;
    private int totalCookTime;
    private boolean isUpdated;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return EtherealFurnaceBlockEntity.this.fuel;
                }
                case 1 -> {
                    return EtherealFurnaceBlockEntity.this.cookTime;
                }
                case 2 -> {
                    return EtherealFurnaceBlockEntity.this.totalCookTime;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> EtherealFurnaceBlockEntity.this.fuel = value;
                case 1 -> EtherealFurnaceBlockEntity.this.cookTime = value;
                case 2 -> EtherealFurnaceBlockEntity.this.totalCookTime = value;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public EtherealFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_FURNACE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        tickEtherCount(world);
        cookingTick(world, state);
        transferTick(world);

        if (isUpdated) {
            world.getChunkManager().markForUpdate(pos);
            isUpdated = false;
        }
    }

    public void cookingTick(ServerWorld world, BlockState state) {
        if (fuel == 0) tryConsumeFuel();
        boolean isCooking = isCooking();
        boolean stateChanged = false;
        if (isCooking) cookTime++;

        if (!isCooking && isCookingValid()) {
            // старт эфирования
            totalCookTime = DEFAULT_COOK_TIME;
            cookTime = 1;
            stateChanged = true;
            markDirty();
        } else if (isCooking && !isCookingValid()) {
            // остановка эфирования
            totalCookTime = 0;
            cookTime = 0;
            stateChanged = true;
            markDirty();
        }

        if (isCooking && cookTime >= totalCookTime && totalCookTime != 0 && isEnoughSpace()) {
            // завершение эфирования
            ItemStack consumedItem = getStack(1);
            float etherPoints = EtherSources.getEtherFuel(consumedItem.getItem());
            increment(etherPoints);
            consumedItem.decrement(1);

            cookTime = 0;
            totalCookTime = 0;
            fuel--;
            stateChanged = !isCookingValid();
            updateCount();
            markDirty();
        }

        if (stateChanged) {
            BlockState newState = state.with(LIT, isCooking());
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
            markDirty();
        }
    }

    @Override
    public void markDirty() {
        isUpdated = true;
        super.markDirty();
    }

    public boolean isCooking() {
        return cookTime != 0;
    }

    public boolean isCookingValid() {
        return fuel != 0 && EtherSources.isEtherSource(getStack(1).getItem());
    }

    public void tryConsumeFuel() {
        ItemStack fuelStack = getStack(0);
        if (!fuelStack.isOf(Items.BLAZE_POWDER)) return;

        fuel = MAX_FUEL;
        fuelStack.decrement(1);
        markDirty();
    }

    public boolean isEnoughSpace() {
        return EtherSources.getEtherFuel(getStack(0).getItem()) + storedEther <= getMaxEther();
    }

    @Override
    public float getMaxEther() {
        return 64;
    }

    @Override
    public float getStoredEther() {
        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public void setStoredEther(float value) {
        storedEther = value;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return false;
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        return Direction.DOWN;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {
        if (world.getTime() % 10 == 0) transfer(world);
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        nbt.putFloat("stored_ether", storedEther);
        nbt.putInt("fuel", fuel);
        nbt.putInt("cook_time", cookTime);
        nbt.putInt("total_cook_time", totalCookTime);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        storedEther = nbt.getFloat("stored_ether");
        Inventories.readNbt(nbt, inventory);
        fuel = nbt.getInt("fuel");
        cookTime = nbt.getInt("cook_time");
        totalCookTime = nbt.getInt("total_cook_time");
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

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EtherealFurnaceScreenHandler(syncId, inv, this, propertyDelegate);
    }

    @Override
    public float getEtherCount() {
        return storedEther;
    }

    @Override
    public List<Integer> getCounterSlots() {
        return Collections.singletonList(2);
    }

    @Override
    public Inventory getInventoryForCounter() {
        return this;
    }

    @Override
    public void tickEtherCount(ServerWorld world) {
        if (world.getTime() % 5 == 0) updateCount();
    }
}

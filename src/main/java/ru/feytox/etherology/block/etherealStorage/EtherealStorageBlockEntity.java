package ru.feytox.etherology.block.etherealStorage;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.glints.AbstractGlintItem;
import ru.feytox.etherology.magic.ether.EtherCounter;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.StopBlockAnimS2C;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.feytox.etherology.block.etherealStorage.EtherealStorageBlock.FACING;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_STORAGE_BLOCK_ENTITY;

public class EtherealStorageBlockEntity extends TickableBlockEntity
        implements EtherStorage, EGeoBlockEntity, ImplementedInventory, NamedScreenHandlerFactory, EtherCounter, SidedInventory {
    private static final RawAnimation OPEN_ANIM;
    private static final RawAnimation CLOSE_ANIM;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private static final float MAX_ETHER = 64f;
    private float storageEther;
    private int viewers = 0;
    private boolean isOpen = false;

    public EtherealStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_STORAGE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
        glintTick(world);
        tickEtherCount(world);
    }

    @Override
    public float getMaxEther() {
        return MAX_ETHER;
    }

    @Override
    public float getStoredEther() {
        return storageEther;
    }

    @Override
    public float getTransportableEther() {
        return storageEther + getGlintEther();
    }

    public float getGlintEther() {
        List<EtherGlint> glints = getGlints();
        float glintEther = 0;
        for (EtherGlint glint : glints) {
            glintEther += glint.getStoredEther();
        }
        return glintEther;
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public void setStoredEther(float value) {
        storageEther = value;
        markDirty();
    }

    @Override
    public float decrement(float value) {
        if (storageEther >= value) return EtherStorage.super.decrement(value);
        if (getGlintEther() >= value) return decrementGlint(value);
        return EtherStorage.super.decrement(value);
    }

    public float incrementGlint(float value) {
        List<EtherGlint> glints = getGlints();
        for (EtherGlint glint : glints) {
            if (!glint.isFull()) {
                value = glint.increment(value);
            }
            if (value == 0) break;
        }

        return value;
    }

    public float decrementGlint(float value) {
        List<EtherGlint> glints = getGlints();
        float needValue = value;
        for (int i = glints.size()-1; i > -1; i--) {
            EtherGlint glint = glints.get(i);
            if (glint.getStoredEther() > 0) {
                needValue -= glint.decrement(value);
            }
            if (needValue == 0) break;
        }

        return value - needValue;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return !side.equals(Direction.DOWN) && !side.equals(Direction.UP) && !side.equals(getCachedState().get(FACING));
    }

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
        if (world.getTime() % 5 == 0) transfer(world);
    }

    public void glintTick(ServerWorld world) {
        if (world.getTime() % 5 != 0 || storageEther == 0) return;

        float newEther = Math.max(0, storageEther - 1);
        float value = storageEther - newEther;
        storageEther = newEther;

        float tipValue = incrementGlint(value);
        storageEther = Math.min(MAX_ETHER, storageEther + tipValue);
    }

    @Override
    public void tickEtherCount(ServerWorld world) {
        if (world.getTime() % 5 == 0) updateCount();
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        nbt.putFloat("storage_ether", storageEther);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storageEther = nbt.getFloat("storage_ether");

        inventory.clear();
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        viewers += 1;
        if (world == null || world.isClient || isOpen) return;
        ServerWorld serverWorld = (ServerWorld) world;
        StopBlockAnimS2C.sendForTracking(this, "close");
        StartBlockAnimS2C.sendForTracking(this, "open");
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, 0.9f);
        isOpen = true;
    }

    @Override
    public void onClose(PlayerEntity player) {
        viewers -= 1;
        if (world == null || world.isClient || !isOpen || viewers > 0) return;
        ServerWorld serverWorld = (ServerWorld) world;
        StopBlockAnimS2C.sendForTracking(this, "open");
        StartBlockAnimS2C.sendForTracking(this, "close");
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5f, 0.9f);
        isOpen = false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(createTriggerController("open", OPEN_ANIM));
        controllers.add(createTriggerController("close", CLOSE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public List<EtherGlint> getGlints() {
        List<EtherGlint> glints = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemStack glintStack = getItems().get(i);
            if (!(glintStack.getItem() instanceof AbstractGlintItem)) continue;
            glints.add(new EtherGlint(glintStack));
        }

        return glints;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.etherology.ethereal_storage.title");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EtherealStorageScreenHandler(syncId, inv, this);
    }

    static {
        OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.ether_storage.open");
        CLOSE_ANIM = RawAnimation.begin().thenPlay("animation.ether_storage.close");
    }

    @Override
    public float getEtherCount() {
        return storageEther;
    }

    @Override
    public List<Integer> getCounterSlots() {
        return Collections.singletonList(3);
    }

    @Override
    public Inventory getInventoryForCounter() {
        return this;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0,1,2};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}

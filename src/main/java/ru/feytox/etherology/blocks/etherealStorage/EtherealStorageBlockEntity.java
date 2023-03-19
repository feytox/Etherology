package ru.feytox.etherology.blocks.etherealStorage;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.items.glints.AbstractGlintItem;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.gecko.EGeoNetwork;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

import static ru.feytox.etherology.BlocksRegistry.ETHEREAL_STORAGE_BLOCK_ENTITY;
import static ru.feytox.etherology.ItemsRegistry.ETHER_SHARD;
import static ru.feytox.etherology.blocks.etherealStorage.EtherealStorageBlock.FACING;

public class EtherealStorageBlockEntity extends TickableBlockEntity
        implements EtherStorage, EGeoBlockEntity, ImplementedInventory, NamedScreenHandlerFactory {
    private static final RawAnimation OPEN_ANIM;
    private static final RawAnimation CLOSE_ANIM;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(12, ItemStack.EMPTY);
    private static final float MAX_ETHER = 64f * 3;
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
        etherItemTick(world);
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

    public void etherItemTick(ServerWorld world) {
        if (world.getTime() % 5 != 0) return;

        int etherValue = MathHelper.floor(storageEther);
        setStack(9, ItemStack.EMPTY);
        setStack(10, ItemStack.EMPTY);
        setStack(11, ItemStack.EMPTY);
        for (int i = 0; i < 3 && etherValue > 0; i++) {
            int count = Math.min(64, etherValue);
            etherValue -= count;
            ItemStack stack = ETHER_SHARD.getDefaultStack();
            stack.setCount(count);
            setStack(9 + i, stack);
        }
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

        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        viewers += 1;
        if (world == null || world.isClient || isOpen) return;
        ServerWorld serverWorld = (ServerWorld) world;
        EGeoNetwork.sendStopAnim(serverWorld, pos, "close");
        EGeoNetwork.sendStartAnim(serverWorld, pos, "open");
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, 0.9f);
        isOpen = true;
    }

    @Override
    public void onClose(PlayerEntity player) {
        viewers -= 1;
        if (world == null || world.isClient || !isOpen || viewers > 0) return;
        ServerWorld serverWorld = (ServerWorld) world;
        EGeoNetwork.sendStopAnim(serverWorld, pos, "open");
        EGeoNetwork.sendStartAnim(serverWorld, pos, "close");
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5f, 0.9f);
        isOpen = false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(getTriggerController("open", OPEN_ANIM));
        controllers.add(getTriggerController("close", CLOSE_ANIM));
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
        for (int i = 0; i < 9; i++) {
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
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}

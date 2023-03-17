package name.uwu.feytox.etherology.blocks.etherealStorage;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.etherology.items.glints.AbstractGlintItem;
import name.uwu.feytox.etherology.magic.ether.EtherGlint;
import name.uwu.feytox.etherology.magic.ether.EtherStorage;
import name.uwu.feytox.etherology.util.feyapi.TickableBlockEntity;
import name.uwu.feytox.etherology.util.gecko.EGeoBlockEntity;
import name.uwu.feytox.etherology.util.gecko.EGeoNetwork;
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
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

import static name.uwu.feytox.etherology.BlocksRegistry.ETHEREAL_STORAGE_BLOCK_ENTITY;

public class EtherealStorageBlockEntity extends TickableBlockEntity
        implements EtherStorage, EGeoBlockEntity, ImplementedInventory, NamedScreenHandlerFactory {
    private static final RawAnimation OPEN_ANIM;
    private static final RawAnimation CLOSE_ANIM;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(12, ItemStack.EMPTY);
    private int viewers = 0;
    private boolean isOpen = false;

    public EtherealStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_STORAGE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
    }

    @Override
    public float getMaxEther() {
        List<EtherGlint> glints = getGlints();
        float maxEther = 0;
        for (EtherGlint glint : glints) {
            maxEther += glint.getMaxEther();
        }

        return maxEther;
    }

    @Override
    public float getStoredEther() {
        List<EtherGlint> glints = getGlints();
        float storedEther = 0;
        for (EtherGlint glint : glints) {
            storedEther += glint.getStoredEther();
        }

        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public float increment(float value) {
        List<EtherGlint> glints = getGlints();
        float tipValue = 0;
        for (EtherGlint glint : glints) {
            if (glint.isFull()) {
                tipValue = glint.increment(value);
                break;
            }
        }

        return tipValue;
    }

    @Override
    public float decrement(float value) {
        List<EtherGlint> glints = getGlints();
        float tipValue = 0;
        for (int i = glints.size()-1; i > -1; i--) {
            EtherGlint glint = glints.get(i);
            if (glint.getStoredEther() > 0) {
                tipValue = glint.decrement(value);
                break;
            }
        }

        return tipValue;
    }

    @Override
    public boolean isInputSide(Direction side) {
        // TODO: 16/03/2023 добавить определение сайда
        return !side.equals(Direction.DOWN);
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
        if (world.getTime() % 20 == 0) transfer(world);
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

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

    @Deprecated
    @Override
    public void setStoredEther(float value) {}

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

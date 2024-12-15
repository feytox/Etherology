package ru.feytox.etherology.block.crate;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.StopBlockAnimS2C;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import static ru.feytox.etherology.registry.block.EBlocks.CRATE_BLOCK_ENTITY;

public class CrateBlockEntity extends BlockEntity implements EGeoBlockEntity, ImplementedInventory, NamedScreenHandlerFactory {

    private static final RawAnimation OPEN_ANIM;
    private static final RawAnimation CLOSE_ANIM;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(10, ItemStack.EMPTY);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int viewers = 0;
    private boolean isOpen = false;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(CRATE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public void onOpen(PlayerEntity player) {
        viewers += 1;
        if (world == null || world.isClient || isOpen) return;
        ServerWorld serverWorld = (ServerWorld) world;
        StopBlockAnimS2C.sendForTrackingOld(this, "close");
        StartBlockAnimS2C.sendForTracking(this, "open");
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, 0.9f);
        isOpen = true;
    }

    @Override
    public void onClose(PlayerEntity player) {
        viewers -= 1;
        if (world == null || world.isClient || !isOpen || viewers > 0) return;
        ServerWorld serverWorld = (ServerWorld) world;
        StopBlockAnimS2C.sendForTrackingOld(this, "open");
        StartBlockAnimS2C.sendForTracking(this, "close");
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5f, 0.9f);
        isOpen = false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, items, registryLookup);
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        items.clear();
        Inventories.readNbt(nbt, items, registryLookup);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CrateScreenHandler(syncId, inv, this);
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    static {
        OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.crate.open");
        CLOSE_ANIM = RawAnimation.begin().thenPlay("animation.crate.close");
    }
}

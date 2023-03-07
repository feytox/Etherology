package name.uwu.feytox.etherology.blocks.closet;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.etherology.furniture.FurnitureData;
import name.uwu.feytox.etherology.util.nbt.Nbtable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.CLOSET_SLAB;
import static name.uwu.feytox.etherology.furniture.AbstractFurSlabBlock.BOTTOM_ACTIVE;
import static name.uwu.feytox.etherology.furniture.AbstractFurSlabBlock.TOP_ACTIVE;

public class ClosetData extends FurnitureData implements ImplementedInventory, ExtendedScreenHandlerFactory {
    private World cachedWorld = null;
    private BlockState cachedState = null;
    private BlockPos cachedPos = null;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY);

    public ClosetData(boolean isBottom) {
        super(isBottom);
    }

    @Override
    public void clientUse(ClientWorld world, BlockState state, BlockPos pos, PlayerEntity player, Hand hand) {
        cache(world, state, pos);
    }

    @Override
    public void serverUse(ServerWorld world, BlockState state, BlockPos pos, PlayerEntity player, Hand hand) {
        cache(world, state, pos);
        player.openHandledScreen(this);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!checkCache()) return;

        playSound(SoundEvents.BLOCK_BARREL_OPEN);
        BlockState oldState = cachedWorld.getBlockState(cachedPos);
        BooleanProperty property = isBottom ? BOTTOM_ACTIVE : TOP_ACTIVE;
        cachedWorld.setBlockState(cachedPos, oldState.with(property, true));
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!checkCache()) return;

        playSound(SoundEvents.BLOCK_BARREL_CLOSE);
        BlockState oldState = cachedWorld.getBlockState(cachedPos);
        BooleanProperty property = isBottom ? BOTTOM_ACTIVE : TOP_ACTIVE;
        cachedWorld.setBlockState(cachedPos, oldState.with(property, false));
    }

    public void playSound(SoundEvent soundEvent) {
        cachedWorld.playSound(null,
                cachedPos.getX(), cachedPos.getY(), cachedPos.getZ(),
                soundEvent, SoundCategory.BLOCKS, 0.5F,
                cachedWorld.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public Nbtable readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.inventory);
        return this;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(CLOSET_SLAB.getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ClosetScreenHandler(syncId, inv, this, isBottom);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
    }

    public void cache(World world, BlockState state, BlockPos pos) {
        cachedWorld = world;
        cachedState = state;
        cachedPos = pos;
    }

    public boolean checkCache() {
        return cachedWorld != null && cachedState != null && cachedPos != null;
    }
}
